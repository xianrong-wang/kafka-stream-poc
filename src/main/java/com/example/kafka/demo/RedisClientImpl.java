package com.example.kafka.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.ReportMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
@Component
public class RedisClientImpl<K, V> implements RedisClient<K, V>
{
    @Autowired
    private ObjectMapper om;

    @Autowired
    private Jedis jedis;
    
    public void write(String key, String value){
        try
        {
            K newK = om.readValue(key, new TypeReference<K>() {
            });
            V newV = om.readValue(value, new TypeReference<V>() {
            });
            this.write(newK, newV);
        }
        catch (JsonProcessingException ex)
        {
            log.error("Failed due to exception: {}", ex.getMessage());
        }
    }
    @Override
    public V read(K key, Class<V> clazz)
    {
        V newV = null;
        try
        {
            String keyString = "";
            if(key instanceof String) {
                keyString = (String)key;
            }
            else
                keyString = om.writeValueAsString(key);
            String value = jedis.get(keyString);
            if(value!=null)
                newV = om.readValue(value, clazz);
        } 
        catch (JsonProcessingException ex)
        {
            log.error("Failed due to exception: {}", ex.getMessage());
        }
        return newV;
    }
    @Override
    public void write(K key, V value)
    {
        try
        {
            String keyString = "";
            if(key instanceof String) {
                keyString = (String)key;
            }
            else {
               keyString = om.writeValueAsString(key); 
            }
            jedis.set(keyString, om.writeValueAsString(value));
        } 
        catch (JsonProcessingException ex)
        {
            log.error("Failed due to exception: {}", ex.getMessage());
        }
        
    }
}
