package com.example.kafka.demo.message.handler;

import java.time.LocalDateTime;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportRequest;

public class ReportReqMessageProcessor extends AbstractMessageProcessor{

    @Override
    public ProcessResult process(Message<?> message) throws UnkownMessageException
    {
        if(!(message.getPayload() instanceof ReportRequest)) {
            throw new UnkownMessageException(message);
        }
        ProcessResult result = ProcessResult.builder().message(message).processStartTime(LocalDateTime.now()).build();
        
        //processing message
        ReportRequest req = (ReportRequest)message.getPayload();
        if(req.isOverwrite()) {
            cleanup();
        }
        //store the result
        result.setProcessEndTime(LocalDateTime.now());
        return result;
    }

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
    
}