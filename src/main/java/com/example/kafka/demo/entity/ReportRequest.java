package com.example.kafka.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReportRequest{
    private String name;
    private LocalDate reportDate;
    private String tenant;
    private String requestUser;
    private LocalDateTime requestDatetime;
    private boolean overwrite;
    private String iptTransactionId;
}