package com.example.kafka.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.RedisClient;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.event.IaEventPublisher;
import com.example.kafka.demo.service.KeyManager;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class RedisListener
{
    @Autowired
    private IaEventPublisher cancelEventPublisher;
    @Autowired
    private RedisClient client;
    
    @Autowired
    private KeyManager keyManager;
    
    @Bean
    RedisMessageListenerContainer getValueChangeListener(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
    
        listenerContainer.addMessageListener((message, pattern) -> {
            log.info("redis event triggered: {}", message);
            // event handling comes here
            if(message.toString().startsWith("Queue:")) {
                final ReportRequest req = client.read(message.toString(),ReportRequest.class);
                log.info("new value: {}",req);
                if(req != null && req.isCancelled())
                    cancelEventPublisher.publishCancelEvent(keyManager.generateReportKey(req));
            }
    
        }, new PatternTopic("__keyevent@*__:set"));
    
        return listenerContainer;
    }
}
