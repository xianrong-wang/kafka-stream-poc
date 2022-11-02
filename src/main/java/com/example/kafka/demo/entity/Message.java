package com.example.kafka.demo.entity;

import lombok.Data;

@Data
public class Message<T>{
    private String key;
    private T payload;
}
