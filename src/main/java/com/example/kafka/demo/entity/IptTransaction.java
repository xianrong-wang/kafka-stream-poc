package com.example.kafka.demo.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IptTransaction
{
    private int id;
    private String eventId;
    private String fromEntityId;
    private String toEntityId;
    private int shares;
}
