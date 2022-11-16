package com.example.kafka.demo.service;

import com.example.kafka.demo.entity.ProcessResult;

public interface StatusAggregator
{
    ProcessResult getProcessResult(String reportKey);
}
