package com.example.kafka.demo.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.config.TopicBuilder;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.json.StatusDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import redis.clients.jedis.Jedis;

@Configuration
public class AppConfig
{
    @Bean
    public ObjectMapper getMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ReportStatus.class, new StatusDeserializer());
       // TypeReference<ProcessResult<Message<?>>> typeRef 
       // = new TypeReference<ProcessResult<Message<?>>>() {};
       // module.addDeserializer(typeRef.getClass(), new ProcessResultDeserializer<Message<?>>());
        objectMapper.registerModule(module);
        
        
        JavaTimeModule javaTimeModule=new JavaTimeModule();
        // Hack time module to allow 'Z' at the end of string (i.e. javascript json's) 
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
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
        return TopicBuilder.name(config.getElimGenPerIptRequest().getTopic()).partitions(3).replicas(2).build();
    }
    
    @Bean
    public NewTopic createReportStatusTopic(KafkaConfig config)
    {
        return TopicBuilder.name(config.getReportStatusUpdRequest().getTopic()).partitions(1).replicas(2).build();
    }
    
    @Bean
    public NewTopic createCancelTopic(KafkaConfig config)
    {
        return TopicBuilder.name(config.getCancelRequest().getTopic()).partitions(1).replicas(2).build();
    }
    
    @Bean
    public NewTopic createTestTopic(KafkaConfig config)
    {
        return TopicBuilder.name(config.getCancelRequest().getTopic()).partitions(2).replicas(2).build();
    }
    
    /*
     * @Bean public NewTopic createProcessResultTopic(KafkaConfig config) { return TopicBuilder.name(config.getProcessResult().getTopic()).partitions(1).replicas(2).build(); }
     */
   
    @Bean @Scope("prototype")
    public Jedis getJedis( 
            @Value("${ia.redis.host:localhost}")
            String host,
            @Value("${ia.redis.port:6379}")
            int port
            ) 
    {
        return new Jedis(host,port);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
    
    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
      return new JedisConnectionFactory();
    }
}
