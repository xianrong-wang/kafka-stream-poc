package com.example.kafka.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.ReportDetailMessage;
import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.processor.ReportDetailProcessor;
import com.example.kafka.demo.processor.ReportProcessor;
import com.example.kafka.demo.redisks.RedisStoreBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaStreamConfig
{
    @Value("${ia.kafka.report-detail.topic:ia-report}")
    private String reportTopic;
    @Value("${ia.kafka.report-detail.state-store:reports}")
    private String reportStateStore;
    
    @Value("${ia.kafka.report-detail.topic:ia-report-detail}")
    private String reportDetailTopic;
    @Value("${ia.kafka.report-detail.state-store:report-details}")
    private String reportDetailStateStore;
    @Autowired
    private RedisClientImpl<String,ReportMessage> redisClient;
    @Autowired
    private RedisClientImpl<String,ReportDetailMessage> redisClient2;
    
    @Autowired
    private  ReportTaskHandler handler;
    
    @Autowired
    private ObjectMapper om;
    
    @Autowired
    public void processReportDetail(StreamsBuilder streamsBuilder) {
        RedisStoreBuilder<String,ReportDetailMessage> customStoreBuilder =
                new RedisStoreBuilder<>(reportDetailStateStore, null, true,redisClient2);
        streamsBuilder.addStateStore(customStoreBuilder)
                .stream(reportDetailTopic, Consumed.with(Serdes.String(), 
                        Serdes.String()))   
                //      new  JsonSerde<ReportMessage>()))
                // Serdes.serdeFrom(new JsonSerializer<LinkedHashMap>(), new JsonDeserializer<LinkedHashMap>())))
                .peek((k,v)->log.info("key:{},val:{}",k,v.toString()))
                .filter((k,v)->v.indexOf("reportDate")!=-1)
                .map((k,v)->{
                    try
                    {
                        return new KeyValue<String, ReportDetailMessage>(k, om.readValue(v, ReportDetailMessage.class));
                    } catch (JsonProcessingException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                })
                .process(()->new ReportDetailProcessor(handler,reportDetailStateStore), new String[]{reportDetailStateStore});
                ;
    }
    
    @Autowired
    public void processReport(StreamsBuilder streamsBuilder) {
        RedisStoreBuilder<String,ReportMessage> customStoreBuilder =
                new RedisStoreBuilder<>(reportStateStore, null, true,redisClient);
        streamsBuilder.addStateStore(customStoreBuilder)
                .stream(reportTopic, Consumed.with(Serdes.String(), 
                        Serdes.String()))   
                //      new  JsonSerde<ReportMessage>()))
                // Serdes.serdeFrom(new JsonSerializer<LinkedHashMap>(), new JsonDeserializer<LinkedHashMap>())))
                .peek((k,v)->log.info("key:{},val:{}",k,v.toString()))
                .filter((k,v)->v.indexOf("reportDate")!=-1)
                .map((k,v)->{
                    try
                    {
                        return new KeyValue<String, ReportMessage>(k, om.readValue(v, ReportMessage.class));
                    } catch (JsonProcessingException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                })
                .process(()->new ReportProcessor(handler,reportStateStore), new String[]{reportStateStore});
                ;
    }
    
}
