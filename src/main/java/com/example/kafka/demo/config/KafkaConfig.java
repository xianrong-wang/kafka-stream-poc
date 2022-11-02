package com.example.kafka.demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.kafka.demo.entity.ReportRequest;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("ia.kafka")
public class KafkaConfig 
{
    private ReportTopicConfig reportRequest;
    private ReportTopicConfig reportDetail;
    private ReportTopicConfig processResult;
    
    private Map<Class<?>,String> typeTopicMap;
    
   @PostConstruct
   void buildMap(){
       typeTopicMap = new HashMap<>();
       typeTopicMap.put(ReportRequest.class, this.reportRequest.getTopic());
       //typeTopicMap.put(ReportRequest.class, this.reportRequest.getTopic());
       //typeTopicMap.put(ReportRequest.class, this.reportRequest.getTopic());
   }
}
