package com.example.kafka.demo.entity;

import lombok.Data;

@Data
public class Message
{
    private String key;
    private String value;
}
