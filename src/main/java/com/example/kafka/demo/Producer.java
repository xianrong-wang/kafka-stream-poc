package com.example.kafka.demo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.config.KafkaConfig;
import com.example.kafka.demo.entity.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class Producer
{
    private final KafkaConfig config;
    private final KafkaTemplate<String, Message<?>> kafkaTemplate;
    
    public void sendMessage(Message<?> m)
    {
        final String topic = config.getTypeTopicMap().get(m.getPayload().getClass());
        kafkaTemplate.send(topic, m.getKey(), m)
        .addCallback(
          result -> log.info("Message:{} was sent to topic: {}", m, topic),
          ex -> log.error("Failed to send message", ex)
        );
        
    }
}
