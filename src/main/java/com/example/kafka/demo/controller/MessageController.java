package com.example.kafka.demo.controller;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.demo.Producer;
import com.example.kafka.demo.RedisClientImpl;
import com.example.kafka.demo.config.KafkaConfig;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportReqResult;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.redisks.ReadableRedisStore;
import com.example.kafka.demo.redisks.RedisStoreType;
import com.fasterxml.jackson.databind.ObjectMapper;


@Slf4j
@RestController
public class MessageController
{
    @Autowired
    private Producer producer;
    @Autowired
    private StreamsBuilderFactoryBean factoryBean;
    @Autowired
    private RedisClientImpl<String,ProcessResult> redisClient;
    @Autowired
    private KafkaConfig config;
    
    @Autowired
    private ObjectMapper om;
    
    @PostMapping("/messages/")
    public String create(@RequestBody Message<ReportRequest> m)
    {
        log.info("create task: {}", m);
        producer.sendMessage(m);
        return "Success!";
    }
    
    @PostMapping("/messages/report")
    public String sendReportRequestMessage(@RequestBody ReportRequest rtpRequest)
    {
        log.info("create message for report request: {}", rtpRequest);
        producer.sendMessage(buildMessage(rtpRequest));
        return "Success!";
    }
    
    private Message<?> buildMessage(ReportRequest rtpRequest)
    {
        rtpRequest.setRequestDatetime(LocalDateTime.now());
        Message<ReportRequest> m = new Message<>();
        m.setKey(generateMessageKey(rtpRequest));
        m.setPayload(rtpRequest);
        return m;
    }

    private String generateMessageKey(ReportRequest rtpRequest)
    {
        return String.join( ":",rtpRequest.getTenant(), 
                rtpRequest.getReportDate().format(DateTimeFormatter.ISO_DATE),
                rtpRequest.getRequestDatetime().format(DateTimeFormatter.ISO_DATE_TIME)
                );
    }

    
    @GetMapping("/messages/status/{key}")
    public Object getReportRequestStatus(@PathVariable String key) {
        
        // Get the Redis store type
        final QueryableStoreType<ReadableRedisStore<String, ProcessResult>> queryableStoreType =
                new RedisStoreType<String, ProcessResult>(redisClient);
        
        KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
        
        final String storeName = config.getReportRequest().getStateStore();
        ReadableRedisStore<String, ProcessResult> store = kafkaStreams
            .store(StoreQueryParameters.fromNameAndType(storeName, queryableStoreType));
        return store.read(key,ProcessResult.class);
    }
}
