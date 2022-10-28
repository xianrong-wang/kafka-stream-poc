package com.example.kafka.demo.processor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import com.example.kafka.demo.ReportTaskHandler;
import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.entity.ReportReqResult;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.redisks.RedisStore;
import com.example.kafka.demo.service.IptService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportProcessor implements Processor<String, ReportMessage, Void, Void> {

    private ProcessorContext<Void, Void> context;
    private RedisStore<String, ReportReqResult> redisStore;
    private ReportTaskHandler handler;
    final String stateStoreName;
    public ReportProcessor(ReportTaskHandler handler,String stateStoreName){
        this.handler = handler;
        this.stateStoreName = stateStoreName; 
    }
    @Override
    public void init(ProcessorContext<Void, Void> context) {
        this.context = context;
        this.redisStore = this.context.getStateStore(stateStoreName);

    }


    @Override
    public void close() {
        // close any resources managed by this processor
        // Note: Do not close any StateStores as these are managed by the library
    }

    @Override
    public void process(Record<String, ReportMessage> record)
    {
        try
        {
            ReportReqResult result = handler.process(record.value(),redisStore);
            log.info("complete processing message: {}", record.value());
            log.info("result: {}", result);
            //redisStore.write(record.key(), result);
        } catch (InterruptedException e)
        {
            //currValue.setStatus(ReportStatus.FAIL);
            //redisStore.write(record.key(), currValue);
            throw new RuntimeException(e);
        }
            
    }
    
  }
