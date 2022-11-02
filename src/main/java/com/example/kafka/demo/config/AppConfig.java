package com.example.kafka.demo.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.json.StatusDeserializer;
import com.example.kafka.demo.processor.ReportDetailProcessor;
import com.example.kafka.demo.processor.MessageProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import redis.clients.jedis.Jedis;

@Configuration
public class AppConfig
{
    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ReportStatus.class, new StatusDeserializer());
        objectMapper.registerModule(module);
        
        JavaTimeModule javaTimeModule=new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's) 
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
    
    @Bean
    public NewTopic createReportTopic(KafkaConfig config)
    {
        return TopicBuilder.name(config.getReportRequest().getTopic()).partitions(3).replicas(2).build();
    }

    @Bean
    public NewTopic createReportDetailTopic(KafkaConfig config)
    {
        return TopicBuilder.name(config.getReportDetail().getTopic()).partitions(3).replicas(2).build();
    }
    
    /*
     * @Bean public NewTopic createProcessResultTopic(KafkaConfig config) { return TopicBuilder.name(config.getProcessResult().getTopic()).partitions(1).replicas(2).build(); }
     */
   
    @Bean
    public Jedis getJedis( 
            @Value("${ia.redis.host:localhost}")
            String host,
            @Value("${ia.redis.port:6379}")
            int port
            ) 
    {
        return new Jedis(host,port);
    }
}
