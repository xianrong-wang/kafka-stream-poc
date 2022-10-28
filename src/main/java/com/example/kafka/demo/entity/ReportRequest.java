package com.example.kafka.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportRequest
{
    private LocalDate reportDate;
    private String tenant;
    private String user;
    private LocalDateTime requestDate;
    private String iptTransactionId;
    private boolean overwrite;
}
