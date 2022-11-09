package com.example.kafka.demo.message.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.event.IaCancelEvent;
import com.example.kafka.demo.service.Cancelable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageCancelImpl implements MessageCancelListener, ApplicationListener<IaCancelEvent>{

    private Map<String, Cancelable> watchList = new ConcurrentHashMap<>();//support concurrent operations
    @Override
    public void register(Message<?> msg, Cancelable cancelable)
    {
        watchList.compute(msg.getKey(), (k,v)->{
                    if(v==null) {
                       v = cancelable;
                    }
                    return v;
                }
            );
        
    }
    @Override
    public void cancel(String msgKey)
    {
        //cancel report request
        //watchList.get(msgKey).cancel();
        //cancel ipt request
        String time = msgKey.split(":")[3];
        watchList.keySet().stream().filter(k->k.equals(msgKey)||k.startsWith("IPT:" + time))
                .peek(k->log.info("cancelling key: {}", k))
                .forEach(k->{
                    Cancelable t = watchList.get(k);
                    if(t!=null) {
                        t.cancel();
                        watchList.remove(k);
                    }
                    });
        
    }
    @Override
    public void unregister(Message<?> msg)
    {
        watchList.remove(msg.getKey());
        
    }
    @Override
    public void onApplicationEvent(IaCancelEvent event)
    {
       this.cancel(event.getMessageKey());
        
    }
    
}