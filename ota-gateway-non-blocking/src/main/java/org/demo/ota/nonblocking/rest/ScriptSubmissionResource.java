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
        final LoggingContext loggingContext = new LoggingContext("submission", seId);

        METRICS.instrumentStage(() -> {
            log.debug("{} Processing {} scripts submission", loggingContext, scripts.size());

            CompletionStage<Long> stage = null; // <-- non final filed, potential concurrent access bug!

            for (int i = 0; i < scripts.size(); i++) {
                final int scriptIndex = i;
                final Script script = scripts.get(scriptIndex);

                if (stage == null) {
                    stage = encryptAndStoreSingleScript(
                            loggingContext,
                            seId,
                            scriptIndex,
                            script);
                } else {
                    stage = stage.thenCompose(ignore -> encryptAndStoreSingleScript(
                            loggingContext,
                            seId,
                            scriptIndex,
                            script));
                }
            }

            return stage;
        })
        .whenComplete((numberOfScripts, e) -> {
            if (e != null) {
                asyncResponse.resume(e);
            } else {
                log.debug("{} Request processed", loggingContext);
                asyncResponse.resume(numberOfScripts);
            }
        });
    }

    private CompletionStage<Long> encryptAndStoreSingleScript(
        final LoggingContext loggingContext,
        final String seId,
        final int scriptIndex,
        final Script script
    ) {
        log.debug("{} Encrypting script {}", loggingContext, scriptIndex);

        return
        secureModuleClient
        .encrypt(seId, script.getPayload())
        .thenCompose(encryptedPayload -> {
            log.debug("{} Storing encrypted script {}", loggingContext, scriptIndex);
            return scriptStorageClient.storeScript(seId, new Script(encryptedPayload));
        });
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }
}
