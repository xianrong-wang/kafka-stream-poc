package com.example.kafka.demo.message.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.example.kafka.demo.Producer;
import com.example.kafka.demo.entity.LedgePost;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.entity.ReportStatusUpdRequest;
import com.example.kafka.demo.service.EliminationService;
import com.example.kafka.demo.service.LedgeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class IptTaskHandler extends AbstractTaskHandler{

    final private EliminationService elimServ;
    final private LedgeService ledgeServ;
    final private Producer producer;
    
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
        if(!(message.getPayload() instanceof IptRequest)) {
            throw new UnkownMessageException(message);
        }
        //processing message
        IptRequest req = (IptRequest)message.getPayload();
        //check whether report request was cancelled?
        ProcessResult parentRequestStatus = this.redisStore.read(req.getOrigReqKey(), ProcessResult.class);
        ProcessResult result = this.redisStore.read(message.getKey(), ProcessResult.class);
        if(parentRequestStatus==null || ReportStatus.CANCEL.compareTo(parentRequestStatus.getStatus())==0) {
            log.info("request was cancelled: {}", parentRequestStatus);
            updateStatus(result, message.getKey(),ReportStatus.CANCEL);
            return;
        }
        
        updateStatus(result, message.getKey(),ReportStatus.PROCESSING);
        
        try
        {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e)
        {
           throw new CancellationException("this task was cancelled");
        }
        
        if(req.isOverwrite()) {
            cleanup();
        }
        //generate IptTransProcessMessage
        List<LedgePost> posts = elimServ.createElimLedgePost(ledgeServ
                .getLedagePost(req.getTransactionId()));
       
       //sink into db
        log.info("persist elimination entries: {}", posts);
        result.setProcessEndTime(LocalDateTime.now());
        updateStatus(result,message.getKey(),ReportStatus.SUCCESS);
        //send finish message to kafka
        Message<ReportStatusUpdRequest> finishMessage = buildFinishMessage(result, message);
        //producer.sendMessage(finishMessage);
    }

    private ProcessResult updateStatus(ProcessResult result, String key, ReportStatus status)
    {
        result.setStatus(status);
        this.redisStore.write(key, result);
        return result;
    }

    private Message<ReportStatusUpdRequest> buildFinishMessage(ProcessResult result,Message<?> message)
    {
        Message<ReportStatusUpdRequest> finishMessage = new Message<ReportStatusUpdRequest>();
        //message = result.getMessage();
        result.getMessage();
        finishMessage.setKey(message.getKey()+":status");
        finishMessage.setPayload(
                ReportStatusUpdRequest.builder().origialKey(((IptRequest)message.getPayload()).getOrigReqKey()).subMessageKey(message.getKey())
                .subMessageStatus(result.getStatus())
                .build()
                );
        return finishMessage;
    }
    
    
}