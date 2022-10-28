package com.example.kafka.demo.redisks;

import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.internals.StateStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.kafka.demo.RedisClientImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;
import java.util.Map;

@Slf4j
public class RedisStoreTypeWrapper<K, V> implements ReadableRedisStore<K, V> {
    private final QueryableStoreType<ReadableRedisStore<K, V>> redisStoreType;
    private final String storeName;
    private final String streamId;
    private final StateStoreProvider provider;
    private RedisClientImpl<K,V> redisClient;

    public RedisStoreTypeWrapper(final StateStoreProvider provider,
                                 final String storeName,
                                 final String streamId,
                                 final QueryableStoreType<ReadableRedisStore<K, V>> redisStoreType,
                                 final RedisClientImpl<K,V> redisClient) {
        this.provider = provider;
        this.storeName = storeName;
        this.streamId = streamId;
        this.redisStoreType = redisStoreType;
        this.redisClient = redisClient;
    }

    @Override
    public V read(K key, Class<V> targetType) {
        return redisClient.read(key,targetType);
    }

    private String readFromStream(String key, Jedis jedis)
    {
        StreamEntryID start = null; // null -> start from the last item in the stream
        StreamEntryID end = null;//new StreamEntryID(0, 0); // end at first item in the stream
        //int count = 1;

        //query the stream range and return by inverted order
        List<redis.clients.jedis.resps.StreamEntry> streamEntries = jedis.xrevrange(this.streamId, end, start);

        if (streamEntries != null) {
            // Get the most recently added item, which is also the last item
            return streamEntries.stream().filter(x->x.getFields().containsKey(key)).findFirst().get().getFields().get(key);
            //StreamEntry entry = streamEntries.get(streamEntries.size() - 1);
            //Map<String, String> fields = entry.getFields();
            //return fields.get(key);
        } else {
            log.warn("No new data in the stream.");
        }
        return null;
    }
}
