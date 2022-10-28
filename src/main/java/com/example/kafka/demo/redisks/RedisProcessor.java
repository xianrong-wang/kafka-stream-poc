package com.example.kafka.demo.redisks;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

public class RedisProcessor implements Processor<String, String, Void, Void> {

    private ProcessorContext<Void, Void> context;
    private RedisStore<String, String> redisStore;

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        this.context = context;
        redisStore = this.context.getStateStore("messages");

    }


    @Override
    public void close() {
        // close any resources managed by this processor
        // Note: Do not close any StateStores as these are managed by the library
    }

    @Override
    public void process(Record<String, String> record)
    {
        String preValue = redisStore.read(record.key(),String.class);
        String newValue = preValue;
        if(newValue!=null) {
            newValue += record.value();
        }
        else {
            newValue = record.value();
        }
        redisStore.write(record.key(), record.value());
        context.commit();
    }

  }
