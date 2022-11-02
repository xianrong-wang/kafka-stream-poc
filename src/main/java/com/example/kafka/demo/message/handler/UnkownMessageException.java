package com.example.kafka.demo.message.handler;

import com.example.kafka.demo.entity.Message;

public class UnkownMessageException extends Exception
{
    private static final long serialVersionUID = 1L;
    private Message<?> message;
    UnkownMessageException(Message<?> message){
        this.message = message;
    }
}