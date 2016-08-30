package org.demo.secure.module;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

@Path("secure-module")
public class SecureModuleResource extends Application {

	private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(100);
	
	@POST
	@Path("/encrypt/{keyDiversifier}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public void encrypt(@PathParam("keyDiversifier") String keyDiversifier, String payload, @Suspended final AsyncResponse asyncResponse) {

		scheduledExecutor.schedule(()->{
			asyncResponse.resume("encrypted:" + keyDiversifier + ":" + payload.toUpperCase());
		}, 1000, TimeUnit.MILLISECONDS);

	}
	
		
    @Override
    public Set<Object> getSingletons() {
    	return new HashSet<Object>(Arrays.asList(this));
    }

}
