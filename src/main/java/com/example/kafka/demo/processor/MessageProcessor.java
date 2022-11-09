package com.example.kafka.demo.processor;

import java.time.LocalDateTime;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.message.handler.MessageHandler;
import com.example.kafka.demo.message.handler.MessageHandlerProvider;
import com.example.kafka.demo.redisks.RedisStore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageProcessor implements Processor<String, Message<?>, Void, Void> {

    private ProcessorContext<Void, Void> context;
    private RedisStore<String, ProcessResult> redisStore;
    private MessageHandlerProvider handlerProvider;
    final String stateStoreName;
    public MessageProcessor(MessageHandlerProvider handlerProvider,String stateStoreName){
        this.handlerProvider = handlerProvider;
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
    public void process(Record<String, Message<?>> record)
    {
        log.info("processing request: {}", record.value().getPayload());
        ProcessResult result = this.redisStore.read(record.key(), ProcessResult.class);
        if(result!=null && ReportStatus.CANCEL.compareTo(result.getStatus())==0) {
            log.info("this message was cancelled: {}", record.value());
            return;
        }
        try
        {
            final MessageHandler messageHandler = handlerProvider.get();
            messageHandler.process(record.value(),this.redisStore);
            
        } catch (CancellationException ex)
        {
            this.redisStore.write(record.key(),
                    ProcessResult.builder().message(record.value()).status(ReportStatus.CANCEL).processEndTime(LocalDateTime.now()).build()
            );
        }
        catch(ExecutionException ex) {
            if(ex.getCause().getClass().equals(CancellationException.class)) {
                this.redisStore.write(record.key(),
                        ProcessResult.builder().message(record.value()).status(ReportStatus.CANCEL).processEndTime(LocalDateTime.now()).build()
                ); 
            }
            else
                throw new ConsolidationException(ex);
        }
        catch (Exception ex) {//why?
            throw new ConsolidationException(ex);
        }
        
    }
    
  }
