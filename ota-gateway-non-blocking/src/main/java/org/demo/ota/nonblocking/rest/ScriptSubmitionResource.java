package org.demo.ota.nonblocking.rest;

import org.demo.ota.nonblocking.model.Script;
import org.demo.ota.nonblocking.security.SecureModuleClient;
import org.demo.ota.nonblocking.storage.ScriptStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@Path("se")
public class ScriptSubmitionResource extends Application {

    private static final Logger log = LoggerFactory.getLogger(ScriptSubmitionResource.class);

    private SecureModuleClient secureModuleClient = SecureModuleClient.instance();
    private ScriptStorageClient scriptStorageClient = ScriptStorageClient.instance();

    @POST
    @Path("/{seId}/scripts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void submitScripts(
            @PathParam("seId") String seId,
            List<Script> scripts,
            @Suspended final AsyncResponse asyncResponse
    ) {

        log.debug("Processing {} scripts submission for seId: {}", scripts.size(), seId);

        encryptAndStoreAllScripts(seId, scripts)
        .whenComplete((numberOfScripts, e) -> {
            if (e != null) {
                asyncResponse.resume(e);
            } else {
                log.debug("Request for seId={} processed", seId);
//                asyncResponse.resume(
//                        Response
//                        .ok(numberOfScripts)
//                        .header(HttpHeaders.CONTENT_TYPE, "text/plain")
//                        .build());
                asyncResponse.resume(numberOfScripts);
            }
        });
    }

    private CompletionStage<Long> encryptAndStoreSingleScript(final String seId, final Script script) {
        return
        secureModuleClient
        .encrypt(seId, script.getPayload())
        .thenCompose(encryptedPayload ->
            scriptStorageClient.storeScript(seId, new Script(encryptedPayload))
        );
    }

    private CompletionStage<Long> encryptAndStoreAllScripts(final String seId, final List<Script> scripts) {
        return encryptAndStoreAllScripts(seId, scripts, 0, null);
    }

    private CompletionStage<Long> encryptAndStoreAllScripts(
            final String seId,
            final List<Script> scripts,
            int index,
            CompletionStage<Long> previousStage
    ) {
        if (index >= scripts.size()) {
            return previousStage;
        } else {

            final Script script = scripts.get(index);
            final CompletionStage<Long> stage;

            if (previousStage == null) {
                stage = encryptAndStoreSingleScript(seId, script);
            } else {
                stage = previousStage.thenCompose(ignore -> encryptAndStoreSingleScript(seId, script));
            }

            return encryptAndStoreAllScripts(seId, scripts, index + 1, stage);
        }
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }
}
