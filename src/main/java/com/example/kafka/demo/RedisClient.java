package com.example.kafka.demo;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public interface RedisClient
{
    public <V, K> V read(K key, Class<V> targetType);
    public <K, V> void write(K key,V value);
    public Set<String> readKeys(String keyPattern);
}
