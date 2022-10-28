package com.example.kafka.demo.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgePost
{
    private int id;
    private String ledgeAccount;
    private BigDecimal amount;
    private LedgePostType postType;
    private String transactionId;
    private String entityId;
}
