package com.example.kafka.demo.controller;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.demo.Producer;
import com.example.kafka.demo.RedisClient;
import com.example.kafka.demo.RedisClientImpl;
import com.example.kafka.demo.config.KafkaConfig;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportReqResult;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.event.IaEventPublisher;
import com.example.kafka.demo.message.handler.MessageCancelListener;
import com.example.kafka.demo.redisks.ReadableRedisStore;
import com.example.kafka.demo.redisks.RedisStoreType;
import com.example.kafka.demo.service.KeyManager;
import com.fasterxml.jackson.databind.ObjectMapper;


@Slf4j
@RestController
public class MessageController
{
    @Autowired
    private Producer producer;
    @Autowired
    private RedisClient redisClient;
    
    @Autowired
    private IaEventPublisher cancelEventPublisher;
    
    
    @Autowired
    private KeyManager keyManager;
    
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
        final Message<?> message = buildMessage(rtpRequest);
        
        //message in queue
        final String messageInQueueKey = keyManager.generateQueueKey(rtpRequest.getTenant());
        ReportRequest inQueueRequest = redisClient.read(messageInQueueKey, ReportRequest.class);
        if(inQueueRequest!=null && !inQueueRequest.isCancelled()) {
            return "Rejected!";
        }
        producer.sendMessage(message);
        redisClient.write(messageInQueueKey, rtpRequest);
        return "Success! view message status from: /messages/report/" + message.getKey();
    }
    //cancel
    @PostMapping("/messages/cancel/{key}")
    public String cancelReportRequestMessage(@PathVariable String key)
    {
        ProcessResult exResult = redisClient.read(key, ProcessResult.class);
        if(exResult!=null && ReportStatus.SUCCESS.compareTo(exResult.getStatus())!=0) {
            exResult.setStatus(ReportStatus.CANCEL);
            redisClient.write(key, exResult);
        }
        log.info("create message for cancel report request: {}", key);
        //producer.sendMessage(buildMessage(rtpRequest));
        //listner.cancel(key);
        //cancelEventPublisher.publishCancelEvent(key);
        
        final String queueKey = keyManager.generateQueueKeyByReportKey(key);
        ReportRequest req = redisClient.read(queueKey, ReportRequest.class);
        if(req!=null) {
            req.setCancelled(true);
            redisClient.write(queueKey,req);
        }
        return "Success!";
    }
    
    private Message<?> buildMessage(ReportRequest rtpRequest)
    {
        rtpRequest.setRequestDatetime(LocalDateTime.now());
        Message<ReportRequest> m = new Message<>();
        m.setKey(keyManager.generateReportKey(rtpRequest));
        m.setPayload(rtpRequest);
        return m;
    }

    public List<ProcessResult> getAllReportRequestStatus(@PathVariable String tenant) {
        
        return redisClient.readKeys(tenant+":*")
        .stream()
        .map(x->redisClient.read(x, ProcessResult.class))
        .collect(Collectors.toList());
    }
    
    @GetMapping("/messages/status/{key}")
    public ProcessResult getReportRequestStatus(@PathVariable String key) {
        
        return redisClient.read(key, ProcessResult.class);
        // Get the Redis store type
        //return getFromStateStore(key);
    }
}
