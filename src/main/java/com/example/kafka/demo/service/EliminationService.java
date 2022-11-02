package com.example.kafka.demo.service;

import java.util.List;

import com.example.kafka.demo.entity.EliminationEntry;
import com.example.kafka.demo.entity.IptTransaction;
import com.example.kafka.demo.entity.LedgePost;

public interface EliminationService
{
    List<LedgePost> createElimLedgePost(List<LedgePost> posts);
    boolean removeElimLedgePost(String fxEntityId);
    boolean removeElimLedgePost(IptTransaction ipt);
}
