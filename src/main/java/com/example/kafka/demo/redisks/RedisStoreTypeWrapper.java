package com.example.kafka.demo.redisks;

import com.example.kafka.demo.RedisClient;

public class RedisStoreTypeWrapper<K, V> implements ReadableRedisStore<K, V> {
    private RedisClient redisClient;

    public RedisStoreTypeWrapper(
                                 final RedisClient redisClient) 
    {
        this.redisClient = redisClient;
    }

    @Override
    public V read(K key, Class<V> targetType) {
        return redisClient.read(key,targetType);
    }

}
