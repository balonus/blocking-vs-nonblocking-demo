package org.demo.ota.nonblocking.security;

import org.apache.http.HttpHeaders;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.util.concurrent.CompletionStage;

public class SecureModuleClient {

    private static final String BASE_URI = "http://localhost:7070/secure-module/encrypt";

    private static final SecureModuleClient INSTANCE = new SecureModuleClient();

    private SecureModuleClient() {
    }

    public static SecureModuleClient instance() {
        return INSTANCE;
    }

    public CompletionStage<String> encrypt(String keyDiversifier, String payload) {

        final AsyncHttpClient client = new DefaultAsyncHttpClient();

        return client
        .preparePost(BASE_URI + "/" + keyDiversifier)
        .setHeader(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
        .setHeader(HttpHeaders.ACCEPT,  "text/plain")
        .setBody(payload)
        .execute()
        .toCompletableFuture()
        .thenApply(resp -> {
           if (resp.getStatusCode() != 200) {
               throw new EncryptionException();
           }

           return resp.getResponseBody();
        });
    }
}
