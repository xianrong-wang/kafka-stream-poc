package com.example.kafka.demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.kafka.demo.entity.CancelRequest;
import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatusUpdRequest;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("ia.kafka")
public class KafkaConfig 
{
    private ReportTopicConfig reportRequest;
    private ReportTopicConfig elimGenPerIptRequest;
    private ReportTopicConfig reportStatusUpdRequest;
    private ReportTopicConfig cancelRequest;
    
    private Map<Class<?>,String> typeTopicMap;
    
   @PostConstruct
   void buildMap(){
       typeTopicMap = new HashMap<>();
       typeTopicMap.put(ReportRequest.class, this.reportRequest.getTopic());
       typeTopicMap.put(IptRequest.class, this.elimGenPerIptRequest.getTopic());
       typeTopicMap.put(ReportStatusUpdRequest.class, this.reportStatusUpdRequest.getTopic());
       typeTopicMap.put(CancelRequest.class, this.cancelRequest.getTopic());
   }
}
