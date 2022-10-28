package com.example.kafka.demo.processor;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import com.example.kafka.demo.ReportTaskHandler;
import com.example.kafka.demo.entity.ReportDetailMessage;
import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.entity.ReportReqResult;
import com.example.kafka.demo.redisks.RedisStore;
import com.example.kafka.demo.service.IptService;

public class ReportDetailProcessor implements Processor<String, ReportDetailMessage, Void, Void> {

    private ProcessorContext<Void, Void> context;
    private RedisStore<String, ReportReqResult> redisStore;
    private ReportTaskHandler handler;
    final String stateStoreName;
    public ReportDetailProcessor(ReportTaskHandler handler,String stateStoreName){
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
    public void process(Record<String, ReportDetailMessage> record)
    {
        //ReportReqResult preValue = redisStore.read(record.key(), ReportReqResult.class);
        //ReportDetailMessage newValue = record.value();
        ReportReqResult result = handler.processReportDetail(record.value(),redisStore);
        //redisStore.write(record.key(), result);
        context.commit();
    }

  }
