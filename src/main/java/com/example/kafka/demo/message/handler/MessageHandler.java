package com.example.kafka.demo.message.handler;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.service.Cancelable;
import com.example.kafka.demo.service.Overwritable;

public interface MessageHandler extends Cancelable, Overwritable
{
    public <T> ProcessResult handle(Message<T> msg,Function<Message<T>, ProcessResult> messageProcessor) throws InterruptedException, ExecutionException;
}
