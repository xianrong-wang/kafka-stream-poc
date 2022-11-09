package com.example.kafka.demo.entity;

import java.time.LocalDate;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//@SpringBootTest(classes = { AppConfig.class}) 
//@EnableAutoConfiguration(exclude = { 
//        DataSourceAutoConfiguration.class,
//        DataSourceTransactionManagerAutoConfiguration.class, 
//        HibernateJpaAutoConfiguration.class, 
//        KafkaAutoConfiguration.class 
//    })
@Slf4j
public class ReportReqResultTest
{
    AppConfig config = new AppConfig();
//    @Autowired
    private ObjectMapper om = config.getMapper();
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
    
    @Test
    void testDeserialization() throws JsonProcessingException {
       Message<ReportRequest> msg = new Message<>();
       msg.setKey("test");
       msg.setPayload(ReportRequest.builder().name("test").tenant("testTenant").reportDate(LocalDate.now()).requestDatetime(LocalDateTime.now()).build());
       final String json = om.writeValueAsString(msg);
       log.info(json);
       String newJson = "{\"message\":{\"key\":\"test:2022-11-22:2022-11-02T14:18:40.165463\",\"payload\":{\"name\":\"report test 01\",\"reportDate\":[2022,11,22],\"tenant\":\"test\",\"requestUser\":\"testUser\",\"requestDatetime\":[2022,11,2,14,18,40,165463000],\"overwrite\":false,\"iptTransactionId\":null}},\"status\":\"SUCCESS\",\"processStartTime\":\"2022-11-02T14:18:40.508371\",\"processEndTime\":\"2022-11-02T14:18:50.509967\"}";
       ProcessResult newMsg = om.readValue(newJson, new TypeReference<ProcessResult>() {});
       log.info("{}", newMsg);
    }
}
