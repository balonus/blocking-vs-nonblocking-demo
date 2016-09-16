package org.demo.ota.blocking.rest;

import org.demo.ota.common.ResourceMetrics;
import org.demo.ota.blocking.model.Script;
import org.demo.ota.blocking.storage.ScriptStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Path("se")
public class ScriptPollingResource extends Application {

    private static final Logger log = LoggerFactory.getLogger(ScriptPollingResource.class);
    private static final ResourceMetrics METRICS = new ResourceMetrics("ota_polling");

    private ScriptStorageClient scriptStorageClient = ScriptStorageClient.instance();

    @GET
    @Path("/{seId}/next-script")
    @Produces(MediaType.APPLICATION_JSON)
    public Script getNextScript(@PathParam("seId") String seId) {
        return METRICS.instrument(() -> {
            log.debug("Looking for next script for seId: {}", seId);

            Script nextScript = scriptStorageClient.nextScript(seId);

            log.debug("Returning next script: {} for seId: {}", nextScript, seId);

            return nextScript;
        });
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }
}
