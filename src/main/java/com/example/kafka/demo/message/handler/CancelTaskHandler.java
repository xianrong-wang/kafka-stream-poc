package com.example.kafka.demo.message.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.CancelRequest;
import com.example.kafka.demo.event.IaEventPublisher;
import com.example.kafka.demo.service.KeyManager;
import com.example.kafka.demo.service.TaskManager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class CancelTaskHandler extends AbstractTaskHandler{

    @Autowired
    private TaskManager tm;
    @Autowired
    private KeyManager km;
    
    @Override
    public void cleanup()
    {
      //resultStore.cleanup();
        
    }

    @Override
    public void cancel()
    {
        // TODO Auto-generated method stub
        
        
    }

    @Override
    public void run(Message<?> message) throws UnkownMessageException
    {
        if(!(message.getPayload() instanceof CancelRequest)) {
            throw new UnkownMessageException(message);
        }
        
        tm.cancel(km.getTenantFromKey(((CancelRequest)message.getPayload()).getMessageKey()));
        //cancelEventPublisher.publishCancelEvent(((CancelRequest)message.getPayload()).getMessageKey());
    }
    
   
    
}