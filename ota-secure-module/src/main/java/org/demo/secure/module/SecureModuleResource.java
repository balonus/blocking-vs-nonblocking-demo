package org.demo.secure.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Path("secure-module")
public class SecureModuleResource extends Application {


    private static final Logger log = LoggerFactory.getLogger(SecureModuleResource.class);
    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(100);

    @POST
    @Path("/encrypt/{keyDiversifier}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void encrypt(
            @PathParam("keyDiversifier") String keyDiversifier,
            String payload,
            @Suspended final AsyncResponse asyncResponse
    ) {

        log.debug("div: {}, payload: {}", keyDiversifier, payload);

        scheduledExecutor.schedule(() -> {
            asyncResponse.resume("encrypted:" + keyDiversifier + ":" + payload.toUpperCase());
        }, 200, TimeUnit.MILLISECONDS); // TODO should be parametrized

    }

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(this);
    }

}
