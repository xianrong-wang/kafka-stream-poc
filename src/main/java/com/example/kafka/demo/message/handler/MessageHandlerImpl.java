package com.example.kafka.demo.message.handler;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.redisks.RedisStore;
import com.example.kafka.demo.service.KeyManager;
import com.example.kafka.demo.service.TaskManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Scope("prototype")
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler{
    
    private final TaskHandlerProvider provider;
    //private ExecutorService executor;
    private final TaskManager tm;
    private final KeyManager km;
    private boolean runAsync = true;
    
    @Override
    public void cancel()
    {
       //executor.shutdownNow();
       log.info("cancelled the message process: {}", this);
    }

    @Override
    public void process(Message<?> msg,RedisStore<String,ProcessResult> redisStore) throws CancellationException, InterruptedException, ExecutionException
    {
        ProcessResult result = ProcessResult.builder().message((Message<Object>) msg).processStartTime(LocalDateTime.now()).status(ReportStatus.PENDING).build();
        redisStore.write(msg.getKey(), result);
        //listener.register(msg, this);
        if(runAsync ) {
            final Runnable task = ()->provider.get(msg.getPayload().getClass(),redisStore).apply(msg);
            //executor.submit(task);//.get();
            final String tenant = km.getTenantFromKey(msg.getKey());
            tm.run(task,tenant);
        }
        else {
            provider.get(msg.getPayload().getClass(),redisStore).apply(msg);
        }
        //listener.unregister(msg);
        
    }

}