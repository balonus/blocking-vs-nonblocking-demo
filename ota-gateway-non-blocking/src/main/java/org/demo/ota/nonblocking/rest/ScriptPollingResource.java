package org.demo.ota.nonblocking.rest;

import org.demo.ota.common.ResourceMetrics;
import org.demo.ota.nonblocking.model.Script;
import org.demo.ota.nonblocking.storage.ScriptStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Set;

@Path("se")
public class ScriptPollingResource extends Application {

    private static final Logger log = LoggerFactory.getLogger(ScriptPollingResource.class);
    private static final ResourceMetrics METRICS = new ResourceMetrics("ota_polling");

    private ScriptStorageClient scriptStorageClient = ScriptStorageClient.instance();

    @GET
    @Path("/{seId}/next-script")
    @Produces(MediaType.APPLICATION_JSON)
    public void getNextScript(@PathParam("seId") String seId, @Suspended AsyncResponse asyncResponse) {

        final DiagnosticContext diagnosticContext = new DiagnosticContext("poll", seId);

        METRICS.instrumentStage(() -> {
            log.debug("{} Looking for next script", diagnosticContext, seId);

            return scriptStorageClient
            .nextScript(seId)
            .thenApply(s -> {
                if (s.isPresent()) {
                    log.debug("{} Script found. Responding back to card", diagnosticContext);
                    return s.get();
                } else {
                    log.debug("{} Script not found", diagnosticContext);
                    throw new NotFoundException();
                }
            });
        })
        .whenComplete((script, e) -> {
            if (e != null) {
                asyncResponse.resume(e);
            } else {
                asyncResponse.resume(new Script(script));
            }
        });
    }

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(this);
    }
}
