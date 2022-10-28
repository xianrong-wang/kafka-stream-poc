package com.example.kafka.demo.entity;

import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.kafka.demo.json.StatusDeserializer;


@Data
@ToString
public class ReportMessage
{
    private String key;
    private String tenant;
    private String name;
    private String reportDate;
    private boolean isOverwrite;//if report was run before, determine whether to rerun it
    private int count;
    @JsonDeserialize(using = StatusDeserializer.class)
    private ReportStatus status = ReportStatus.NONE;
    private long createdTimestamp;
    private long updatedTimestamp;
}


