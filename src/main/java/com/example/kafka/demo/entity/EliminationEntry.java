package com.example.kafka.demo.entity;

import lombok.Data;

@Data
public class EliminationEntry extends LedgePost
{
    int id;
    String entityId;
    
}
