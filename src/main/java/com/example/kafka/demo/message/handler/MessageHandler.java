package com.example.kafka.demo.message.handler;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.springframework.context.ApplicationListener;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.event.IaCancelEvent;
import com.example.kafka.demo.redisks.RedisStore;
import com.example.kafka.demo.service.Cancelable;
import com.example.kafka.demo.service.Overwritable;

public interface MessageHandler extends ApplicationListener<IaCancelEvent>,Cancelable, Overwritable
{
    public void process(Message<?> msg, RedisStore<String,ProcessResult> redisStore) throws InterruptedException, ExecutionException;
}
