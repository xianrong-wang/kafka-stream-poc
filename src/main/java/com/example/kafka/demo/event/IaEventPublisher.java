package com.example.kafka.demo.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class IaEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCancelEvent(final String messsageKey) {
        log.info("publishing cancel event: {}", messsageKey);
        IaCancelEvent cancelEvent = new IaCancelEvent(this, messsageKey);
        applicationEventPublisher.publishEvent(cancelEvent);
    }
}