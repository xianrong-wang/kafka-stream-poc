package com.example.kafka.demo.message.handler;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.service.Cancelable;

public interface MessageCancelListener
{

    void register(Message<?> msg, Cancelable cancelable);
    void cancel(String msgKey);
    void unregister(Message<?> msg);

}
