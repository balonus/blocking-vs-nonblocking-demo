package org.demo.ota.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.demo.ota.model.Script;
import org.demo.ota.storage.ScriptStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("se")
public class ScriptPollingResource extends Application {

	private static final Logger log = LoggerFactory.getLogger(ScriptPollingResource.class);

	private ScriptStorageClient scriptStorageClient = ScriptStorageClient.instance();

	@GET
	@Path("/{seId}/next-script")
	@Produces(MediaType.APPLICATION_JSON)
	public Script getNextScript(@PathParam("seId") String seId) {
		
		log.debug("Looking for next script for seId: {}", seId);
		
		Script nextScript = scriptStorageClient.nextScript(seId);

		log.debug("Returning next script: {} for seId: {}", nextScript, seId);

		return nextScript;
		
	}
	
		
    @Override
    public Set<Object> getSingletons() {
    	return new HashSet<Object>(Arrays.asList(this));
    }

}
