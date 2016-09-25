package org.demo.ota.blocking.rest;

import org.demo.ota.blocking.model.Script;
import org.demo.ota.blocking.security.client.SecureModuleClient;
import org.demo.ota.blocking.storage.ScriptStorageClient;
import org.demo.ota.common.ResourceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    public long submitScripts(@PathParam("seId") String seId, List<Script> scripts) {
        return METRICS.instrument(() -> {
            log.debug("Processing {} scripts submission for seId: {}", scripts.size(), seId);

            scripts.forEach((script) -> {
                log.debug("Processing script submission - seId: {}, script: {}", seId, script);

                String encryptedPayload = secureModuleClient.encrypt(seId, script.getPayload());
                script.setPayload(encryptedPayload);

                scriptStorageClient.storeScript(seId, script);
            });

            long numberOfScripts = scriptStorageClient.numberOfScriptsForSe(seId);

            log.debug("Total number of scripts for seId: {} is {}", seId, numberOfScripts);

            return numberOfScripts;
        });
    }

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(this);
    }
}
