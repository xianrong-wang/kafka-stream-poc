package com.example.kafka.demo.redisks;

import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.internals.StateStoreProvider;

import com.example.kafka.demo.RedisClient;
import com.example.kafka.demo.RedisClientImpl;

public class RedisStoreType<K, V> implements QueryableStoreType<ReadableRedisStore<K, V>> {
    private RedisClient redisClient;

    public RedisStoreType(RedisClient redisClient)
    {
        this.redisClient = redisClient;
    }

    // Only accept StateStores that are of type RedisStore
    @Override
    public boolean accepts(StateStore stateStore) {
        return stateStore instanceof RedisStore;
    }

    @Override
    public ReadableRedisStore<K, V> create(final StateStoreProvider storeProvider, final String storeName) {
        return new RedisStoreTypeWrapper<K, V>(redisClient);
    }
}
