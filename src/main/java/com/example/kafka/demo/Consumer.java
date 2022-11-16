package com.example.kafka.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Consumer
{
    @KafkaListener(topics = "test", groupId = "foo")
    public void listenGroupFoo(String message) {
        log.info("Received Message in group foo: {}", message);
    }

}
