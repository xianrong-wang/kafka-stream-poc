package com.example.kafka.demo.service;

import java.util.List;

import com.example.kafka.demo.entity.IptTransaction;
import com.example.kafka.demo.entity.LedgePost;

public interface LedgeService
{
    List<LedgePost> getLedagePost(IptTransaction transaction);
    List<LedgePost> getLedagePost(String transactionId);
}
