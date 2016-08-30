package org.demo.ota.nonblocking.security;

import java.util.concurrent.CompletionStage;

public class SecureModuleClient {

    private static final SecureModuleClient INSTANCE = new SecureModuleClient();
//    private Client client;
//    private final URI secureModuleUri;

    private SecureModuleClient() {

//        try {
//            secureModuleUri = new URI(System.getProperty("secureModuleUri", "http://localhost:7070"));
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }

//        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
//        client = new ResteasyClientBuilder().httpEngine(new ApacheHttpClient4Engine(httpClient)).build();

    }

    public static SecureModuleClient instance() {
        return INSTANCE;
    }

    public CompletionStage<String> encrypt(String keyDiversifier, String payload) {
        throw new UnsupportedOperationException();
//        WebTarget target = client.target(secureModuleUri).path("/secure-module/encrypt/{keyDiversifier}").resolveTemplate("keyDiversifier", keyDiversifier);
//
//        Response response = null;
//        try {
//            response = target.request().post(Entity.text(payload));
//            if (response.getStatus() < 200 || response.getStatus() > 299) {
//                throw new RuntimeException("Failed HTTP error code " + response.getStatus());
//            }
//            return response.readEntity(String.class);
//        } finally {
//            if (response != null) {
//                response.close();
//            }
//        }
    }
}
