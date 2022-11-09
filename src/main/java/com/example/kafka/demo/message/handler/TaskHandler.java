package com.example.kafka.demo.message.handler;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;

public interface TaskHandler
{
    void run(Message<?> message) throws UnkownMessageException;
}


