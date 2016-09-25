package org.demo.ota.nonblocking.rest;

import org.demo.ota.common.ResourceMetrics;
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
public class ScriptSubmissionResource extends Application {

    private static final Logger log = LoggerFactory.getLogger(ScriptSubmissionResource.class);
    private static final ResourceMetrics METRICS = new ResourceMetrics("ota_submission");

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
        METRICS.instrumentStage(() -> {
            log.debug("Processing {} scripts submission for seId: {}", scripts.size(), seId);
            return encryptAndStoreAllScripts(seId, scripts);
        })
        .whenComplete((numberOfScripts, e) -> {
            if (e != null) {
                asyncResponse.resume(e);
            } else {
                log.debug("Request for seId={} processed", seId);
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
        assert ! scripts.isEmpty();

        CompletionStage<Long> stage = encryptAndStoreSingleScript(seId, scripts.get(0));

        for (int i = 1; i < scripts.size(); i++) {
            final Script script = scripts.get(i);
            stage = stage.thenCompose(ignore -> encryptAndStoreSingleScript(seId, script));
        }

        return stage;
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }
}
