package com.example.kafka.demo;

import org.springframework.stereotype.Component;

@Component
public interface RedisClient<K, V>
{
    public V read(K key, Class<V> targetType);
    public void write(K key,V value);
}
