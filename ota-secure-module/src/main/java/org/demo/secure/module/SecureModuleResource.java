package org.demo.secure.module;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Path("secure-module")
public class SecureModuleResource extends Application {

    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(100);

    @POST
    @Path("/encrypt/{keyDiversifier}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void encrypt(@PathParam("keyDiversifier") String keyDiversifier, String payload, @Suspended final AsyncResponse asyncResponse) {

        scheduledExecutor.schedule(() -> {
            asyncResponse.resume("encrypted:" + keyDiversifier + ":" + payload.toUpperCase());
        }, 1000, TimeUnit.MILLISECONDS);

    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Collections.singletonList(this));
    }

}
