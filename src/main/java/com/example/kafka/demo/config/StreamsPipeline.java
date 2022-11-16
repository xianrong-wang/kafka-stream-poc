package com.example.kafka.demo.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.RedisClient;
import com.example.kafka.demo.RedisClientImpl;
import com.example.kafka.demo.entity.CancelRequest;
import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatusUpdRequest;
import com.example.kafka.demo.message.handler.MessageHandler;
import com.example.kafka.demo.message.handler.MessageHandlerProvider;
import com.example.kafka.demo.processor.MessageProcessor;
import com.example.kafka.demo.redisks.RedisStoreBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component
public class StreamsPipeline
{
    @Autowired
    private KafkaConfig config;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private MessageHandlerProvider handlerProvider;

    @Autowired
    private ObjectMapper om;

    @Autowired
    public void buildReportReqStream(StreamsBuilder generalKafkaStreamsBuilder)
    {
        final String stateStoreName = config.getReportRequest().getStateStore();
        RedisStoreBuilder<String, ReportRequest> customStoreBuilder = new RedisStoreBuilder<>(stateStoreName, false, redisClient);

        // configure deserializer
        /* having issue to use this, need to add customize JsonDeserializer
        JsonDeserializer<Message<ReportRequest>> deserializer = new JsonDeserializer<>(om);
        deserializer.setUseTypeHeaders(false);
        deserializer.addTrustedPackages("*"); // have to set the trust package otherwise will run into exception
         */
        generalKafkaStreamsBuilder
                .addStateStore(customStoreBuilder)
                .stream(config.getReportRequest().getTopic(), Consumed.with(Serdes.String(), Serdes.String()))//Serdes.serdeFrom(new JsonSerializer<Message<ReportRequest>>(), deserializer)))
                .peek((k, v) -> log.info("key:{}, val:{}", k, v))
                .map(getMapper(ReportRequest.class))   
                .process( ()->new MessageProcessor(handlerProvider,stateStoreName), stateStoreName);
        ;
    }
    
    @Autowired
    public void buildIptReqStream(StreamsBuilder generalKafkaStreamsBuilder)
    {
        final String stateStoreName = config.getElimGenPerIptRequest().getStateStore();
        RedisStoreBuilder<String, IptRequest> customStoreBuilder = new RedisStoreBuilder<>(stateStoreName, false, redisClient);

        generalKafkaStreamsBuilder
                .addStateStore(customStoreBuilder)
                .stream(config.getElimGenPerIptRequest().getTopic(), Consumed.with(Serdes.String(), Serdes.String()))//Serdes.serdeFrom(new JsonSerializer<Message<ReportRequest>>(), deserializer)))
                .peek((k, v) -> log.info("key:{}, val:{}", k, v))
                .map(getMapper(IptRequest.class))   
                .process( ()->new MessageProcessor(handlerProvider,stateStoreName), stateStoreName);
        ;
    }
    
    @Autowired
    public void buildStatusUpdReqStream(StreamsBuilder generalKafkaStreamsBuilder)
    {
        final String stateStoreName = config.getReportStatusUpdRequest().getStateStore();
        RedisStoreBuilder<String, ReportStatusUpdRequest> customStoreBuilder = new RedisStoreBuilder<>(stateStoreName, false, redisClient);

        generalKafkaStreamsBuilder
                .addStateStore(customStoreBuilder)
                .stream(config.getReportStatusUpdRequest().getTopic(), Consumed.with(Serdes.String(), Serdes.String()))//Serdes.serdeFrom(new JsonSerializer<Message<ReportRequest>>(), deserializer)))
                .peek((k, v) -> log.info("key:{}, val:{}", k, v))
                .map(getMapper(ReportStatusUpdRequest.class))   
                .process( ()->new MessageProcessor(handlerProvider,stateStoreName), stateStoreName);
        ;
    }
    
    @Autowired
    public void buildCancelReqStream(StreamsBuilder cancelKafkaStreamsBuilder)
    {
        final String stateStoreName = config.getReportStatusUpdRequest().getStateStore();
        RedisStoreBuilder<String, ReportStatusUpdRequest> customStoreBuilder = new RedisStoreBuilder<>(stateStoreName, false, redisClient);

        cancelKafkaStreamsBuilder
                .addStateStore(customStoreBuilder)
                .stream(config.getCancelRequest().getTopic(), Consumed.with(Serdes.String(), Serdes.String()))//Serdes.serdeFrom(new JsonSerializer<Message<ReportRequest>>(), deserializer)))
                .peek((k, v) -> log.info("key:{}, val:{}", k, v))
                .map(getMapper(CancelRequest.class))   
                .process( ()->new MessageProcessor(handlerProvider,stateStoreName), stateStoreName);
        ;
    }

    private <T> KeyValueMapper<String, String, KeyValue<String, Message<?>>> getMapper(Class<T> requestType)
    {
        return (k,v)->{
            try { 
                final Message<T> message = new Message<>();
                JsonNode node = om.readTree(v);
                message.setKey(node.get("key").asText());
                T obj = om.readValue(node.get("payload").toString(),requestType);
                message.setPayload(obj);
                return new KeyValue<String, Message<?>>(k, message);
            }
            catch(Exception e) 
            { 
                throw new DeserializationException(k, v.getBytes(), false, e);
            } 
        };
    }

}
