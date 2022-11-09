package com.example.kafka.demo.message.handler;

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
import com.example.kafka.demo.event.IaCancelEvent;
import com.example.kafka.demo.redisks.RedisStore;

import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
@Component
@Slf4j
public class MessageHandlerImpl implements MessageHandler{
    
    private final TaskHandlerProvider provider;
    private final MessageCancelListener listener;
    private ExecutorService executor;
    private Message<?> msg;
    private boolean runAsync = true;
    @Autowired
    public MessageHandlerImpl(MessageCancelListener listener,TaskHandlerProvider provider) {
        executor = Executors.newSingleThreadExecutor();
        //register this to processWatcher
        this.listener = listener;
        this.provider = provider;
    }
    
    @Override
    public void cancel()
    {
       executor.shutdownNow();
       log.info("cancelled the message process: {}", this);
    }

    @Override
    public void process(Message<?> msg,RedisStore<String,ProcessResult> redisStore) throws CancellationException, InterruptedException, ExecutionException
    {
        this.msg = msg;
        listener.register(msg, this);
        if(runAsync ) {
            final Callable<Boolean> task = ()->provider.get(msg.getPayload().getClass(),redisStore).apply(msg);
            executor.submit(task).get();
            //submit task and wait for completion
        }
        else {
            provider.get(msg.getPayload().getClass(),redisStore).apply(msg);
        }
        listener.unregister(msg);
        
    }

    @Override
    public void cleanup()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onApplicationEvent(IaCancelEvent event) {
        /*
        log.info("received cancel event for key: {}", event.getMessageKey());
        if(this.msg!=null) {
            log.info("current processing message key: {}",msg.getKey());
            final String sourceKey = this.msg.getKey();
            //cancel report request task
            if(sourceKey.equals(event.getMessageKey()))
                this.cancel();
            //cancel ipt request task
            String time = sourceKey.split(":")[3];
            if(sourceKey.startsWith("IPT:" + time)) {
                this.cancel();
            }
            //cancel status update task
            if(sourceKey.startsWith("IPT:" + time) && sourceKey.endsWith("Status")) {
                this.cancel();
            }
        }
        */
    }
    
}