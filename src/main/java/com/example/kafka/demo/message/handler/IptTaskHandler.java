package com.example.kafka.demo.message.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.Producer;
import com.example.kafka.demo.entity.IptTransaction;
import com.example.kafka.demo.entity.LedgePost;
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.entity.ReportStatusUpdRequest;
import com.example.kafka.demo.entity.SubMessageResult;
import com.example.kafka.demo.service.EliminationService;
import com.example.kafka.demo.service.IptService;
import com.example.kafka.demo.service.LedgeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
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
        if(parentRequestStatus==null || ReportStatus.CANCEL.compareTo(parentRequestStatus.getStatus())==0) {
            log.info("request was cancelled: {}", parentRequestStatus);
            return;
        }
        
        ProcessResult result = ProcessResult.builder().message(message).processStartTime(LocalDateTime.now()).status(ReportStatus.PROCESSING)
                //.subMessageKeys(new ArrayList<>())
                .build();
        this.redisStore.write(message.getKey(), result);
        
        if(req.isOverwrite()) {
            cleanup();
        }
        //generate IptTransProcessMessage
        List<LedgePost> posts = elimServ.createElimLedgePost(ledgeServ
                .getLedagePost(req.getTransactionId()));
        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e)
        {
           throw new CancellationException("this task was cancelled");
        }
       //sink into db
        log.info("persist elimination entries: {}", posts);
        result.setStatus(ReportStatus.SUCCESS);
        result.setProcessEndTime(LocalDateTime.now());
        this.redisStore.write(message.getKey(), result);
        //send finish message to kafka
        Message<ReportStatusUpdRequest> finishMessage = buildFinishMessage(result);
        producer.sendMessage(finishMessage);
    }

    private Message<ReportStatusUpdRequest> buildFinishMessage(ProcessResult result)
    {
        Message<ReportStatusUpdRequest> finishMessage = new Message<ReportStatusUpdRequest>();
        
        final Message<?> message = result.getMessage();
        finishMessage.setKey(message.getKey()+":status");
        finishMessage.setPayload(
                ReportStatusUpdRequest.builder().origialKey(((IptRequest)message.getPayload()).getOrigReqKey()).subMessageKey(message.getKey())
                .subMessageStatus(result.getStatus())
                .build()
                );
        return finishMessage;
    }
    
    
}