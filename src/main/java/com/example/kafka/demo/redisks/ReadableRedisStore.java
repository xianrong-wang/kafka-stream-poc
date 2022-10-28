package com.example.kafka.demo.redisks;

//Read-only interface for MyCustomStore
public interface ReadableRedisStore<K,V> {
    public V read(K key, Class<V> clazz);
}
