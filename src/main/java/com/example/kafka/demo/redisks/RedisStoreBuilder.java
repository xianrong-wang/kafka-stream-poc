package com.example.kafka.demo.redisks;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.state.StoreBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.kafka.demo.RedisClientImpl;

public class RedisStoreBuilder<K, V> implements StoreBuilder<RedisStore<K, V>> {

    private boolean enableCaching = true;
    private final String name;
    private final String streamId;

    private final Map<String, String> logConfig = new HashMap<>();
    private boolean loggingEnabled;

    private RedisClientImpl<K,V> redisClient;
    @Autowired
    public RedisStoreBuilder(String name, String streamId, boolean loggingEnabled,RedisClientImpl<K,V> redisClient) {
        this.name = name;
        this.streamId = streamId;
        this.loggingEnabled = loggingEnabled;
        this.redisClient = redisClient;
    }

    @Override
    public StoreBuilder<RedisStore<K, V>> withCachingEnabled() {
        this.enableCaching = true;
        return this;
    }

    @Override
    public StoreBuilder<RedisStore<K, V>> withCachingDisabled() {
        this.enableCaching = false;
        return this;
    }

    @Override
    public StoreBuilder<RedisStore<K, V>> withLoggingEnabled(Map<String, String> config) {
        loggingEnabled = true;
        return this;
    }

    @Override
    public StoreBuilder<RedisStore<K, V>> withLoggingDisabled() {
        this.loggingEnabled = false;
        return this;
    }

    @Override
    public RedisStore<K, V> build() {
        return new RedisStore<>(this.name, this.streamId, this.loggingEnabled,this.redisClient);
    }

    @Override
    public Map<String, String> logConfig() {
        return logConfig;
    }

    @Override
    public boolean loggingEnabled() {
        return loggingEnabled;
    }

    @Override
    public String name() {
        return name;
    }
}
