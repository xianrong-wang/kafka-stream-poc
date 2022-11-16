package com.example.kafka.demo.entity;

import java.time.LocalDateTime;

import com.example.kafka.demo.json.StatusDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xfmh16x
 * request of generating elimination per IPT transaction
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IptRequest
{
    private String key;//to remove
    private String transactionId;
    private String tenant;
    private ReportStatus status;
    private String reportDate;//to remove
    private String origReqKey;
    private LocalDateTime requestDatetime;
    private boolean isOverwrite;
}
