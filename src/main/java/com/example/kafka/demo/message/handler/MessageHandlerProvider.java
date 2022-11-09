package com.example.kafka.demo.message.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Component
public class MessageHandlerProvider
{
    private final ApplicationContext appContext;
    public MessageHandler get() {
        return appContext.getBean(MessageHandler.class);
    }
}
