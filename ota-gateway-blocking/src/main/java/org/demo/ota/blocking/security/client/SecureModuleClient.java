package org.demo.ota.blocking.security.client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

public final class SecureModuleClient {

    private static final SecureModuleClient INSTANCE = new SecureModuleClient();
    private static final URI BASE_URI = URI.create(System.getenv("SECURE_MODULE_URL"));

    private final Client client;

    private SecureModuleClient() {
        final PoolingHttpClientConnectionManager connManager =
                new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(2000); // TODO should be parametrized
        connManager.setMaxTotal(4000); // TODO should be parametrized

        final HttpClient httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(connManager)
                .build();

        client = new ResteasyClientBuilder()
                .httpEngine(new ApacheHttpClient4Engine(httpClient))
                .build();
    }

    public static SecureModuleClient instance() {
        return INSTANCE;
    }

    public String encrypt(String keyDiversifier, String payload) {

        final WebTarget target = client
                .target(BASE_URI)
                .path("/secure-module/encrypt/{keyDiversifier}")
                .resolveTemplate("keyDiversifier", keyDiversifier);

        Response response = null;
        try {
            response = target.request().post(Entity.text(payload));
            if (response.getStatus() < 200 || response.getStatus() > 299) {
                throw new EncryptionException(
                        "HTTP request failed with status: " + response.getStatus());
            }
            return response.readEntity(String.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
