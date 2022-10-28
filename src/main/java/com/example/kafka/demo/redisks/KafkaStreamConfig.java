package com.example.kafka.demo.redisks;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

//@Configuration
public class KafkaStreamConfig
{
    
    //@Bean
    public StreamsBuilder getDefaultKafkaStreamsBuilder() {
        String storeName = "messages";
        String redisStateStoreName = storeName;
        String redisStreamId = "test";
        StreamsBuilder sb = new StreamsBuilder();
        RedisStoreBuilder<String,String> customStoreBuilder =
                new RedisStoreBuilder<>(redisStateStoreName, redisStreamId, true, null);
       
        sb.addStateStore(customStoreBuilder)
        .stream("ia-report",Consumed.with(Serdes.String(),Serdes.String()))
        .process(
                new ProcessorSupplier<String,String,Void,Void>() {
            @Override
            public Processor<String,String,Void,Void> get()
            {
                return new RedisProcessor();
            }
            
        }, new String[]{"messages"});
        return sb;
    };
    
   
}
