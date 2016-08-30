package org.demo.ota.nonblocking.storage;

import org.demo.ota.nonblocking.model.Script;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class ScriptStorageClient {

    private static final ScriptStorageClient INSTANCE = new ScriptStorageClient();

    private ScriptStorageClient() {
//        try {
//            pool = new JedisPool(new URI(System.getProperty("redisUri", "redis://192.168.99.100:6379")));
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static ScriptStorageClient instance() {
        return INSTANCE;
    }

    public CompletionStage<Void> storeScript(String seId, Script script) {
        throw new UnsupportedOperationException();
    }

    public CompletionStage<Long> numberOfScriptsForSe(String seId) {
        throw new UnsupportedOperationException();
    }

    public CompletionStage<Optional<String>> nextScript(String seId) {
        throw new UnsupportedOperationException();
    }
}
