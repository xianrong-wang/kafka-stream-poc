package com.example.kafka.demo;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
@Component
public class RedisClientImpl implements RedisClient
{
    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApplicationContext context;
    
//    @Autowired
//    private Jedis jedis;
    
    
    @Override
    public <V, K> V read(K key, Class<V> clazz)
    {
        V newV = null;
        try
        {
            String keyString = serializeKey(key);
            String value = getJedis().get(keyString);
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
    public <K, V> void write(K key, V value)
    {
        try
        {
            String keyString = serializeKey(key);
            getJedis().set(keyString, om.writeValueAsString(value));
        } 
        catch (JsonProcessingException ex)
        {
            log.error("Failed due to exception: {}", ex.getMessage());
        }
        
    }

    private <K> String serializeKey(K key) throws JsonProcessingException
    {
        String keyString = "";
        if(key instanceof String) {
            keyString = (String)key;
        }
        else {
           keyString = om.writeValueAsString(key); 
        }
        return keyString;
    }

    @Override
    public Set<String> readKeys(String keyPattern)
    {
        return getJedis().keys(keyPattern);
    }
    
    private Jedis getJedis() {
        return this.context.getBean(Jedis.class);//preventing multi-thread read/write timeout issue
    }
}
