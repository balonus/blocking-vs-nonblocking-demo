package org.demo.ota.nonblocking.storage;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import org.demo.ota.nonblocking.model.Script;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public final class ScriptStorageClient {

    private static final ScriptStorageClient INSTANCE = new ScriptStorageClient();

    private final RedisAsyncCommands<String, String> commands;

    private ScriptStorageClient() {
        commands = RedisClient.create(System.getenv("REDIS_NODES")).connect().async();
    }

    public static ScriptStorageClient instance() {
        return INSTANCE;
    }

    public CompletionStage<Void> storeScript(String seId, Script script) {
        return commands.rpush(seId, script.getPayload()).thenApply(ignore -> null);
    }

    public CompletionStage<Optional<String>> nextScript(String seId) {
        return commands.lpop(seId).thenApply(Optional::ofNullable);
    }

    public CompletionStage<Long> numberOfScriptsForSe(String seId) {
        return commands.llen(seId);
    }

}
