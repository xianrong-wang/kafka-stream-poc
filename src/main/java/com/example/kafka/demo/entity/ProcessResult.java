package com.example.kafka.demo.entity;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessResult{
    private Message<?> message;
    private String status;
    private LocalDateTime processStartTime;
    private LocalDateTime processEndTime;
}