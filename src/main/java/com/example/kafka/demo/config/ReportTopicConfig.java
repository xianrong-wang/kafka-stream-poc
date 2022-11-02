package com.example.kafka.demo.config;

import lombok.Data;

@Data
public class ReportTopicConfig{
    private String topic;
    private int partitions;
    private int replica;
    private String stateStore;
}