package org.demo.ota.nonblocking.rest;

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
import java.util.HashSet;
import java.util.Set;

@Path("se")
public class ScriptPollingResource extends Application {

    private static final Logger log = LoggerFactory.getLogger(ScriptPollingResource.class);

    private ScriptStorageClient scriptStorageClient = ScriptStorageClient.instance();

    @GET
    @Path("/{seId}/next-script")
    @Produces(MediaType.APPLICATION_JSON)
    public void getNextScript(@PathParam("seId") String seId, @Suspended AsyncResponse asyncResponse) {

        log.debug("Looking for next script for seId: {}", seId);

        scriptStorageClient
        .nextScript(seId)
        .thenApply(sOpt -> {
            if (! sOpt.isPresent()) {
                throw new NotFoundException();
            }

            return sOpt.get();
        })
        .thenAccept(asyncResponse::resume)
        .whenComplete((ignore, e) -> {
            if (e != null) {
                asyncResponse.resume(e);
            }
        });
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }

}
