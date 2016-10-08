package org.demo.ota.blocking.storage;

import org.demo.ota.blocking.model.Script;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public final class ScriptStorageClient {

    private static final ScriptStorageClient INSTANCE = new ScriptStorageClient();

    private final JedisPool pool;

    private ScriptStorageClient() {
        try {
            pool = new JedisPool(new URI(System.getenv("REDIS_NODES")));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ScriptStorageClient instance() {
        return INSTANCE;
    }

    public void storeScript(String seId, Script script) {
        try (Jedis jedis = pool.getResource()) {
            jedis.rpush(seId, script.getPayload());
        }
    }

    public long numberOfScriptsForSe(String seId) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.llen(seId);
        }
    }

    public Optional<Script> nextScript(String seId) {
        try (Jedis jedis = pool.getResource()) {
            return Optional.ofNullable(jedis.lpop(seId)).map(Script::new);
        }
    }
}
