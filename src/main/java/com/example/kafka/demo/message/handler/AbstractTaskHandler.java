package com.example.kafka.demo.message.handler;

import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.redisks.RedisStore;
import com.example.kafka.demo.service.Cancelable;
import com.example.kafka.demo.service.Overwritable;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractTaskHandler implements TaskHandler, Overwritable, Cancelable{
    @Getter @Setter
    RedisStore<String,ProcessResult> redisStore;
}