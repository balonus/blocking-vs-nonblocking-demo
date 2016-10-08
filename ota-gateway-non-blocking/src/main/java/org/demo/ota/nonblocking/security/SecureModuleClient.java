package org.demo.ota.nonblocking.security;

import org.apache.http.HttpHeaders;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.util.concurrent.CompletionStage;

public final class SecureModuleClient {

    private static final String BASE_URI = System.getenv("SECURE_MODULE_URL") + "/secure-module/encrypt";

    private static final SecureModuleClient INSTANCE = new SecureModuleClient();

    private final AsyncHttpClient client = new DefaultAsyncHttpClient();

    private SecureModuleClient() {
    }

    public static SecureModuleClient instance() {
        return INSTANCE;
    }

    public CompletionStage<String> encrypt(String keyDiversifier, String payload) {

        return client
        .preparePost(BASE_URI + "/" + keyDiversifier)
        .setHeader(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
        .setHeader(HttpHeaders.ACCEPT,  "text/plain")
        .setBody(payload)
        .execute()
        .toCompletableFuture()
        .thenApply(resp -> {
           if (resp.getStatusCode() < 200 && resp.getStatusCode() > 299) {
               throw new EncryptionException(
                       "HTTP request failed with status: " + resp.getStatusCode());
           }

           return resp.getResponseBody();
        });
    }
}
