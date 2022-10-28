package com.example.kafka.demo.service;

import java.util.List;

import com.example.kafka.demo.entity.IptTransaction;

public interface IptService
{
    List<IptTransaction> getIptTransactions(String tenant,String reportDate);
}
