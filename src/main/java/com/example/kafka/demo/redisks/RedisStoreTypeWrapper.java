package com.example.kafka.demo.redisks;

import com.example.kafka.demo.RedisClientImpl;

public class RedisStoreTypeWrapper<K, V> implements ReadableRedisStore<K, V> {
    private RedisClientImpl<K,V> redisClient;

    public RedisStoreTypeWrapper(
                                 final RedisClientImpl<K,V> redisClient) 
    {
        this.redisClient = redisClient;
    }

    @Override
    public V read(K key, Class<V> targetType) {
        return redisClient.read(key,targetType);
    }

}
