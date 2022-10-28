package com.example.kafka.demo.redisks;

// Read-write interface for RedisStore
public interface WriteableRedisStore<K,V> {
    void write(K key, V value);
}
