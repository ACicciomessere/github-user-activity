package model;

import java.io.File;
import java.io.IOException;

import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;
import redis.embedded.core.RedisServerBuilder;

public class GithubCacheManager {
    private final int port;
    private Jedis jedis;
    private final boolean usingEmbedded;
    private RedisServer redisServer;
    private volatile boolean cacheAvailable = false;

    public GithubCacheManager(int port) {
        this.port = port;

        File dir = new File("/tmp/redis");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        boolean externalRedisAvailable = isRedisRunning(port);
        if (!externalRedisAvailable) {
            redisServer = new RedisServerBuilder()
                    .setting("save 1 100")
                    .setting("appendonly yes")
                    .setting("dir /tmp/redis")
                    .port(port)
                    .build();
        }

        usingEmbedded = !externalRedisAvailable;
        // Defer creating Jedis until startServices to catch connection issues cleanly
    }

    private boolean isRedisRunning(int port) {
        try (Jedis testConn = new Jedis("localhost", port)) {
            return "PONG".equalsIgnoreCase(testConn.ping());
        } catch (Exception e) {
            return false;
        }
    }

    public void startServices() throws IOException {
        if (usingEmbedded && this.redisServer != null && !this.redisServer.isActive()) {
            try {
                this.redisServer.start();
            } catch (RuntimeException e) {
                // Could not start embedded Redis
            }
        }
        try {
            this.jedis = new Jedis("localhost", port);
            // Attempt to ping to validate connection
            this.cacheAvailable = "PONG".equalsIgnoreCase(this.jedis.ping());
        } catch (Exception e) {
            this.cacheAvailable = false;
        }
        if (!this.cacheAvailable && usingEmbedded && this.redisServer != null && this.redisServer.isActive()) {
            // Stop embedded if it started but connection failed for some reason
            this.redisServer.stop();
        }
        if (!this.cacheAvailable) {
            // No Redis; operate in no-cache mode
            System.err.println("Redis cache unavailable on port " + port + ", running without cache.");
        }
    }

    public void storeCache(String key, String value) {
        if (!cacheAvailable) return;
        if (!key.isBlank() && !value.isBlank()) {
            try {
                String res = this.jedis.setex(key, 30, value);
                System.out.println("store cache res:" + res);
            } catch (Exception ignore) {
                // ignore cache errors
            }
            return;
        }
        System.err.println("Error caching: blank key or value");
    }

    public String getFromCache(String key) {
        if (!cacheAvailable) return null;
        if (!key.isEmpty()) {
            try {
                String res = this.jedis.get(key);
                System.out.println("get cache :" + (res == null ? "empty" : "ok"));
                if (res != null && this.jedis.ttl(key) > 0)
                    return res;
            } catch (Exception ignore) {
                // ignore cache errors
            }
            return null;
        }
        System.err.println("Error caching: blank key");
        return null;
    }

    public void removeFromCache(String key) {
        if (!cacheAvailable) return;
        if (!key.isBlank()) {
            try { this.jedis.del(key); } catch (Exception ignore) {}
            return;
        }
        System.err.println("Error caching: blank key");
    }

    public void clearCache() {
        if (!cacheAvailable) return;
        try { this.jedis.flushAll(); } catch (Exception ignore) {}
    }

    public void stopServices() throws IOException {
        try {
            if (this.jedis != null) this.jedis.close();
        } catch (Exception ignore) {}
        if (usingEmbedded && this.redisServer != null && this.redisServer.isActive()) {
            this.redisServer.stop();
        }
        this.cacheAvailable = false;
    }
}
