package com.example.kafka.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReportStatusUpdRequest
{
    private String origialKey;
    private String subMessageKey;
    private ReportStatus subMessageStatus;
}
