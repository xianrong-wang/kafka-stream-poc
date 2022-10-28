package com.example.kafka.demo.entity;

import com.example.kafka.demo.json.StatusDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReportDetailMessage
{
    private String key;
    private String transactionId;
    @JsonDeserialize(using = StatusDeserializer.class)
    private ReportStatus status;
    private String reportDate;
}
