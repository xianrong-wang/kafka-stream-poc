package com.example.kafka.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.entity.ReportReqResult;
import com.example.kafka.demo.redisks.ReadableRedisStore;
import com.example.kafka.demo.redisks.RedisStoreType;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private RedisClientImpl<String,String> redisClient;
    @Autowired
    private RedisClientImpl<String,ReportReqResult> redisClient2;
    
    @Autowired
    private ObjectMapper om;
    
    @PostMapping("/messages/")
    public String create(@RequestBody Message m)
    {
        log.info("create task: {}", m);
        producer.sendMessage(m);
        return "Success!";
    }
    
    @PostMapping("/reports/")
    public String createReport(@RequestBody ReportMessage report) throws JsonProcessingException
    {
        log.info("create report: {}", report);
        producer.sendMessage(report);
        return "Success!";
    }
    
    @GetMapping("/messages/")
    public List<KeyValue<String,String>> getAllMessage() {
        KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
        List<KeyValue<String,String>> messages = new ArrayList<>();
        ReadOnlyKeyValueStore<String, String> counts = kafkaStreams
            .store(StoreQueryParameters.fromNameAndType("messages", QueryableStoreTypes.keyValueStore()));
        counts.all().forEachRemaining(x->{
            log.info("key:{},val:{}",x.key,x.value);
            messages.add(x);
        });
        return messages;
    }
    
    @GetMapping("/messages/{key}")
    public Object getString(@PathVariable String key) {
        
     // Get the Redis store type
        final QueryableStoreType<ReadableRedisStore<String, String>> queryableStoreType =
                new RedisStoreType<String, String>("test",redisClient);
        
        KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
        
        final String storeName = "messages";
        ReadableRedisStore<String, String> store = kafkaStreams
            .store(StoreQueryParameters.fromNameAndType(storeName, queryableStoreType));
        //store.all().forEachRemaining(x->log.info("key:{},val:{}",x.key,x.value));
        return store.read(key,String.class);
    }
    
    @GetMapping("/reports/{key}")
    public Object getReport(@PathVariable String key) {
        
     // Get the Redis store type
        final QueryableStoreType<ReadableRedisStore<String, ReportReqResult>> queryableStoreType =
                new RedisStoreType<String, ReportReqResult>("test",redisClient2);
        
        KafkaStreams kafkaStreams =  factoryBean.getKafkaStreams();
        
        final String storeName = "reports";
        ReadableRedisStore<String, ReportReqResult> store = kafkaStreams
            .store(StoreQueryParameters.fromNameAndType(storeName, queryableStoreType));
        //store.all().forEachRemaining(x->log.info("key:{},val:{}",x.key,x.value));
        return store.read(key,ReportReqResult.class);
    }
}
