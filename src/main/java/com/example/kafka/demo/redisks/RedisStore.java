package com.example.kafka.demo.redisks;

import org.apache.kafka.streams.processor.StateStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.RedisClient;
import com.example.kafka.demo.RedisClientImpl;
import com.example.kafka.demo.entity.ReportMessage;

import org.apache.kafka.streams.processor.ProcessorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisStore<K, V> implements StateStore, WriteableRedisStore<K, V>, ReadableRedisStore<K, V> {
    private String name;
    private boolean open = true;
    private boolean loggingEnabled = false;
    private boolean flushed;
    
    private RedisClient redisClient;
    
    public RedisStore(String name, boolean loggingEnabled,RedisClient redisClient) {
        this.name = name;
        this.loggingEnabled = loggingEnabled;
        this.redisClient = redisClient;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public void init(ProcessorContext context, StateStore root) {
        if (root != null) {
            // register the store
            context.register(root, (key, value) -> {
               this.redisClient.write(key.toString(),value.toString());
            });
        }

        this.open = true;
    }

    @Override
    public void flush() {
        flushed = true;
    }

    @Override
    public void close() {
        open = false;
    }

    @Override
    public boolean persistent() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return open;
    }
    
    

    @Override
    public void write(K key, V value) {
        this.redisClient.write(key, value);
    }

    @Override
    public V read(K key, Class<V> clazz) {
       return (V) this.redisClient.read(key,clazz);
    }
}
