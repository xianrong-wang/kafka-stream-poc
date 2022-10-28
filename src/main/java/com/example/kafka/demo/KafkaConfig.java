package com.example.kafka.demo;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.example.kafka.demo.processor.ReportDetailProcessor;
import com.example.kafka.demo.processor.ReportProcessor;

@Configuration
public class KafkaConfig
{
    @Bean
    NewTopic createReportTopic(
            @Value("${ia.kafka.report-detail.topic:ia-report}")
            String reportTopic) 
    {
           return TopicBuilder.name( reportTopic).partitions(3).replicas(2).build();
    }
    
    @Bean
    NewTopic createReportDetailTopic(
            @Value("${ia.kafka.report-detail.topic:ia-report-detail}")
            String reportDetailTopic
            ) 
    {
           return TopicBuilder.name(reportDetailTopic).partitions(3).replicas(2).build();
    }
    
    @Bean
    public ReportProcessor getReportProcessor(ReportTaskHandler handler,  
            @Value("${ia.kafka.report.state-store:reports}")
            String reportStateStore) {
        return new ReportProcessor(handler,reportStateStore);
    }
    
    @Bean
    public ReportDetailProcessor getReportDetailProcessor(ReportTaskHandler handler,  
            @Value("${ia.kafka.report-detail.state-store:report-details}")
            String reportStateStore) {
        return new ReportDetailProcessor(handler,reportStateStore);
    }
    
}
