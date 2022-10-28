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
public class ReportReqResult
{
   private ReportRequest reportRequest;
   private LocalDateTime startTime;
   private LocalDateTime endTime;
   private ReportStatus status;
   private List<String> messages;
   private List<ReportReqResult> subReqResult;
    
}
