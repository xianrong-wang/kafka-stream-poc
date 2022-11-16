package com.example.kafka.demo.message.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.CancelRequest;
import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatusUpdRequest;
import com.example.kafka.demo.redisks.RedisStore;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class TaskHandlerProvider
{
    static Map<Class<?>,Class<?>> map;
   
    private final ApplicationContext appContext;

    Function<Message<?>,Boolean> get(Class<?> taskType, RedisStore<String, ProcessResult> redisStore)
    {
        buildMap();
        List<?> handlers = 
                Stream.of(appContext.getBeanNamesForType(AbstractTaskHandler.class))
        .map(x->appContext.getBean(x))
        .filter(x->x.getClass().equals(map.get(taskType)))
        .collect(Collectors.toList());
        
        if(handlers.isEmpty())//bad configuration should stop the application
            throw new RuntimeException("bad configuration, no task handler found for this task type: " + taskType);
        
        AbstractTaskHandler handler =  (AbstractTaskHandler)handlers.get(0);
        return (Message<?> msg) -> {
            try
            {
                handler.setRedisStore(redisStore);
                handler.run(msg);
            } 
            catch (UnkownMessageException e)//unlikely happen, should be caught during the message deserialization
            {
                log.error("unknow message", e);
                throw new RuntimeException(e);
            }
            return true;
        };
    }
    
    private void buildMap(){
        if(map==null)
        {
            map = new HashMap<>();
            map.put(ReportRequest.class, ReportTaskHandler.class);
            map.put(IptRequest.class, IptTaskHandler.class);
            map.put(ReportStatusUpdRequest.class, ReportStatusUpdTaskHandler.class);
            map.put(CancelRequest.class, CancelTaskHandler.class);
        }
     }
}
