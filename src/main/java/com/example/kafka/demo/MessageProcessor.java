package com.example.kafka.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.redisks.RedisProcessor;
import com.example.kafka.demo.redisks.RedisStoreBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class MessageProcessor
{
    @Autowired
    private RedisClientImpl<String,String> redisClient;
    @SuppressWarnings("static-access") @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        String storeName = "messages";
        String redisStateStoreName = storeName;
        String redisStreamId = "test";
        RedisStoreBuilder<String,String> customStoreBuilder =
                new RedisStoreBuilder<>(redisStateStoreName, redisStreamId, true,redisClient);
        streamsBuilder.addStateStore(customStoreBuilder)
                .stream("ia-messages", Consumed.with(Serdes.String(), Serdes.String()))
                .peek((k,v)->log.info("key:{},val:{}",k,v))
                //.groupByKey()
                //.reduce((aggValue, newValue) -> aggValue+newValue)
                //.toStream()
                .process(
                        new ProcessorSupplier<String,String,Void,Void>() {
                    @Override
                    public Processor<String,String,Void,Void> get()
                    {
                        return new RedisProcessor();
                    }
                    
                }, new String[]{"messages"});
                ;
    }

    private KeyValueBytesStoreSupplier getStoreSupplier(String storeName)
    {
        KeyValueBytesStoreSupplier storeSupplier = //Stores.persistentKeyValueStore(storeName);
        Stores.inMemoryKeyValueStore(storeName);
        return storeSupplier;
    }
}
