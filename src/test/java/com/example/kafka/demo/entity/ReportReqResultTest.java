package com.example.kafka.demo.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.kafka.demo.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = { AppConfig.class}) 
@EnableAutoConfiguration(exclude = { 
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, 
        HibernateJpaAutoConfiguration.class, 
        KafkaAutoConfiguration.class 
    })
@Slf4j
public class ReportReqResultTest
{
    @Autowired
    private ObjectMapper om;
    @Test
    void testSerialization() throws JsonProcessingException {
        ReportReqResult result = ReportReqResult.builder().startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(ReportStatus.SUCCESS)
                .build();
        ReportReqResult result2 = ReportReqResult.builder().startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(ReportStatus.SUCCESS)
                .build();
        final List<ReportReqResult> asList = Arrays.asList(result2);
        result.setSubReqResult(asList);
       log.info(om.writeValueAsString(result));
    }
}
