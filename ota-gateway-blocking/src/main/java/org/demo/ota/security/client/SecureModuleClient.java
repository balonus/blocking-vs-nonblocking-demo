package org.demo.ota.security.client;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

public class SecureModuleClient {

	private static final SecureModuleClient INSTANCE = new SecureModuleClient();
	private final Client client; 
	private final URI secureModuleUri;
	
	private SecureModuleClient() {
		
		try {
			secureModuleUri = new URI(System.getProperty("secureModuleUri", "http://localhost:7070"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
		client = new ResteasyClientBuilder().httpEngine(new ApacheHttpClient4Engine(httpClient)).build();
		
	};
	
	public static SecureModuleClient instance() {
		return INSTANCE;
	}
	
	public String encrypt(String keyDiversifier, String payload) {
		
		WebTarget target = client.target(secureModuleUri).path("/secure-module/encrypt/{keyDiversifier}").resolveTemplate("keyDiversifier", keyDiversifier);
		
		Response response = null;
		try {
			response = target.request().post(Entity.text(payload));
			if(response.getStatus() < 200 || response.getStatus() > 299 ) {
				throw new RuntimeException("Failed HTTP error code " + response.getStatus());
			}
			return response.readEntity(String.class);
		} finally {
			if(response!=null) {
				response.close();
			}
		}
		
	}
	
}
