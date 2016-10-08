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
        final DiagnosticContext diagnosticContext = new DiagnosticContext("submission", seId);

        METRICS.instrumentStage(() -> {
            log.debug("{} Processing {} scripts submission", diagnosticContext, scripts.size());

            return
            encryptAndStoreAllScripts(diagnosticContext, seId, scripts)
            .thenCompose(
                ignore -> scriptStorageClient.numberOfScriptsForSe(seId)
            );
        })
        .whenComplete((numberOfScripts, e) -> {
            if (e != null) {
                asyncResponse.resume(e);
            } else {
                log.debug("{} Request processed", diagnosticContext);
                asyncResponse.resume(numberOfScripts);
            }
        });
    }

    private CompletionStage<Void> encryptAndStoreAllScripts(
            final DiagnosticContext diagnosticContext,
            final String seId,
            final List<Script> scripts
    ) {
        CompletionStage<Void> stage = null; // <- non final field, potential concurrent access bug!

        for (int i = 0; i < scripts.size(); i++) {
            final int scriptIndex = i;
            final Script script = scripts.get(scriptIndex);

            if (stage == null) {
                stage = encryptAndStoreSingleScript(diagnosticContext, seId, scriptIndex, script);
            } else {
                stage = stage.thenCompose(ignore ->
                        encryptAndStoreSingleScript(diagnosticContext, seId, scriptIndex, script));
            }
        }

        return stage;
    }

    private CompletionStage<Void> encryptAndStoreSingleScript(
            final DiagnosticContext diagnosticContext,
            final String seId,
            final int scriptIndex,
            final Script script
    ) {
        log.debug("{} Encrypting script {}", diagnosticContext, scriptIndex);

        return secureModuleClient
        .encrypt(seId, script.getPayload())
        .thenCompose(
            encryptedPayload -> {
                log.debug("{} Storing encrypted script {}", diagnosticContext, scriptIndex);
                return scriptStorageClient.storeScript(seId, new Script(encryptedPayload));
            }
        );
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }
}
