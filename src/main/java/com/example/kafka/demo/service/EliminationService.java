package com.example.kafka.demo.service;

import java.util.List;

import com.example.kafka.demo.entity.EliminationEntry;
import com.example.kafka.demo.entity.LedgePost;

public interface EliminationService
{
    List<LedgePost> generateElimLedgePost(List<LedgePost> posts);
}
