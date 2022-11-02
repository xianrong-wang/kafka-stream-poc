package com.example.kafka.demo.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.RedisClientImpl;
import com.example.kafka.demo.ReportTaskHandler;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ReportDetailMessage;
import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.processor.ReportDetailProcessor;
import com.example.kafka.demo.processor.MessageProcessor;
import com.example.kafka.demo.redisks.RedisStoreBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j @Component
public class KafkaStreamConfig
{
    @Autowired
    private KafkaConfig config;
    @Autowired
    private RedisClientImpl<String, ReportMessage> redisClient;
    @Autowired
    private RedisClientImpl<String, ReportDetailMessage> redisClient2;

    @Autowired
    private ReportTaskHandler handler;

    @Autowired
    private ObjectMapper om;

    @Autowired
    public void buildReportReqStream(StreamsBuilder streamsBuilder)
    {
        RedisStoreBuilder<String, ?> customStoreBuilder = new RedisStoreBuilder<>(config.getReportRequest().getStateStore(), null, true, redisClient);

        // configure deserializer
        JsonDeserializer<Message<?>> deserializer = new JsonDeserializer<>();
        deserializer.setRemoveTypeHeaders(false); // may not need
        deserializer.addTrustedPackages("*"); // have to set the trust package otherwise will run into exception

        streamsBuilder.addStateStore(customStoreBuilder)
                .stream(config.getReportRequest().getTopic(), Consumed.with(Serdes.String(), Serdes.serdeFrom(new JsonSerializer<Message<?>>(), deserializer)))
                .peek((k, v) -> log.info("key:{},val:{}", k, v))
        
        
          .process( ()->new MessageProcessor(handler,config.getReportRequest().getStateStore()), new String[]{config.getReportRequest().getStateStore()} );
         
         
        ;
    }
    /*
     * @Autowired public void processReport(StreamsBuilder streamsBuilder) { RedisStoreBuilder<String,ReportMessage> customStoreBuilder = new
     * RedisStoreBuilder<>(config.getReportRequest().getStateStore(), null, true,redisClient); streamsBuilder.addStateStore(customStoreBuilder)
     * .stream(config.getReportRequest().getTopic(), Consumed.with(Serdes.String(), Serdes.String())) // new JsonSerde<ReportMessage>())) // Serdes.serdeFrom(new
     * JsonSerializer<LinkedHashMap>(), new JsonDeserializer<LinkedHashMap>()))) .peek((k,v)->log.info("key:{},val:{}",k,v.toString()))
     * .filter((k,v)->v.indexOf("reportDate")!=-1) .map((k,v)->{ try { return new KeyValue<String, ReportMessage>(k, om.readValue(v, ReportMessage.class)); } catch
     * (JsonProcessingException e) { // TODO Auto-generated catch block e.printStackTrace(); } return null; }) .process(()->new
     * ReportProcessor(handler,config.getReportRequest().getStateStore()), new String[]{config.getReportRequest().getStateStore()}); ; }
     * 
     * @Autowired public void processReportDetail(StreamsBuilder streamsBuilder) { RedisStoreBuilder<String,ReportDetailMessage> customStoreBuilder = new
     * RedisStoreBuilder<>(config.getReportDetail().getStateStore(), null, true,redisClient2); streamsBuilder.addStateStore(customStoreBuilder)
     * .stream(config.getReportDetail().getTopic(), Consumed.with(Serdes.String(), Serdes.String())) // new JsonSerde<ReportMessage>())) // Serdes.serdeFrom(new
     * JsonSerializer<LinkedHashMap>(), new JsonDeserializer<LinkedHashMap>()))) .peek((k,v)->log.info("key:{},val:{}",k,v.toString()))
     * .filter((k,v)->v.indexOf("reportDate")!=-1) .map((k,v)->{ try { return new KeyValue<String, ReportDetailMessage>(k, om.readValue(v, ReportDetailMessage.class)); } catch
     * (JsonProcessingException e) { // TODO Auto-generated catch block e.printStackTrace(); } return null; }) .process(()->new
     * ReportDetailProcessor(handler,config.getReportDetail().getStateStore()), new String[]{config.getReportDetail().getStateStore()}); ; }
     */

}
