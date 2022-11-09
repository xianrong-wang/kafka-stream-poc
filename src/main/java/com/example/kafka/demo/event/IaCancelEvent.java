package com.example.kafka.demo.event;

import org.springframework.context.ApplicationEvent;

import com.example.kafka.demo.entity.Message;

import lombok.Getter;

public class IaCancelEvent extends ApplicationEvent
{

    private static final long serialVersionUID = 1L;
    @Getter
    private String messageKey;
    public IaCancelEvent(Object source, String messagekey)
    {
        super(source);
        this.messageKey = messagekey;
    }

}
