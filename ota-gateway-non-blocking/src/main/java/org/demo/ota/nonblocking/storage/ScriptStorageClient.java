package org.demo.ota.nonblocking.storage;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import org.demo.ota.nonblocking.model.Script;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class ScriptStorageClient {

    private static final ScriptStorageClient INSTANCE = new ScriptStorageClient();

    private final RedisAsyncCommands<String, String> commands;

    private ScriptStorageClient() {
        commands = RedisClient.create("redis://192.168.99.100:6379").connect().async();
    }

    public static ScriptStorageClient instance() {
        return INSTANCE;
    }

    public CompletionStage<Long> storeScript(String seId, Script script) {
        return commands.rpush(seId, script.getPayload());
    }

    public CompletionStage<Optional<String>> nextScript(String seId) {
        return commands.lpop(seId).thenApply(Optional::ofNullable);
    }
}
