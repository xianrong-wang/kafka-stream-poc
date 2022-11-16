package com.example.kafka.demo.service.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.example.kafka.demo.service.TaskManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskManagerImpl implements TaskManager
{

    static private Map<String,ExecutorService> executorMap = new ConcurrentHashMap<>();
    @Override
    public Future<?> run(Runnable run, String taskId)
    {
        final ExecutorService executor = getExecutor(taskId,true).get();
        if(!executor.isShutdown()) {
            log.info("running task: {} from the executor: {}", taskId, executor);
            return executor.submit(run);
        }
        else {
            log.info("the executor is shutdown already: {}", executor);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public void cancel(String taskId)
    {
        
        getExecutor(taskId, false).ifPresentOrElse(e->{
            if(!e.isShutdown()) {
                log.info("shutdown task: {} from executor: {}", taskId, e); 
                e.shutdownNow();
                log.info("executor: {} is shutdown", e); 
            }
            else {
                log.info("already shutdown task: {} from executor: {}", taskId, e);  
            }
            executorMap.remove(taskId);
        }, ()->{
            log.info("task: {} not found",taskId);
        });
       
    }
    
    private Optional<ExecutorService> getExecutor(String taskId, boolean created) {
        ExecutorService executor =  executorMap.compute(taskId, (k,v)->{
            log.info("get executor for task: {}", k);
            if(v==null && created) {
                v = Executors.newCachedThreadPool();
            }
            return v;
        });
        return executor==null?Optional.empty():Optional.of(executor);
    }

}
