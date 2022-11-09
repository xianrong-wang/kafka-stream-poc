package com.example.kafka.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProcessResult{
    private Message<?> message;
    private ReportStatus status;
    private LocalDateTime processStartTime;
    private LocalDateTime processEndTime;
    private int totalSubMessages;
    private List<SubMessageResult> subMessageResults;//SubMessageResult should be this be same as ProcessResult
}