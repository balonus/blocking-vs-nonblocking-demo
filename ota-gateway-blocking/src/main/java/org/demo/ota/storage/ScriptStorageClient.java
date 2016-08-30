package org.demo.ota.storage;

import org.demo.ota.model.Script;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;

public class ScriptStorageClient {

    private static final ScriptStorageClient INSTANCE = new ScriptStorageClient();

    private final JedisPool pool;

    private ScriptStorageClient() {
        try {
            pool = new JedisPool(new URI(System.getProperty("redisUri", "redis://192.168.99.100:6379")));
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

    public Script nextScript(String seId) {
        try (Jedis jedis = pool.getResource()) {
            String payload = jedis.lpop(seId);
            return (payload != null) ? new Script(payload) : null;
        }
    }

}
