package com.example.kafka.demo.json;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import com.example.kafka.demo.config.AppConfig;
import com.example.kafka.demo.entity.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageSerializer implements Serializer<Message<?>>
{
    private final ObjectMapper objectMapper = new AppConfig().getMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Message<?> data) {
        try {
            if (data == null){
                log.error("Null received at serializing");
                return null;
            }
            //log.info("Serializing...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Message<?> to byte[]");
        }
    }

    @Override
    public void close() {
    }

}
