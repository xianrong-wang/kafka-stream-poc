package com.example.kafka.demo.message.handler;

import java.time.LocalDateTime;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;

public class MessageHandlerImpl implements MessageHandler{

    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public void cancel()
    {
       executor.shutdownNow();
    }

    @Override
    public void cleanup()
    {
        //resultStore.cleanup();
        
    }

    @Override
    public <T> ProcessResult handle(Message<T> msg,Function<Message<T>, ProcessResult> messageProcessor) throws InterruptedException, ExecutionException
    {
       try
        {
            return executor.submit(()->messageProcessor.apply(msg)).get();
        } 
        catch (CancellationException e)
        {
            return ProcessResult.builder().message(msg).status("cancel").processEndTime(LocalDateTime.now()).build();
        }
    }


    
}