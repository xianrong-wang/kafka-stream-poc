package com.example.kafka.demo.message.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.entity.ReportStatusUpdRequest;
import com.example.kafka.demo.entity.SubMessageResult;
import com.example.kafka.demo.service.KeyManager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
@Slf4j
@AllArgsConstructor
@Component
public class ReportStatusUpdTaskHandler extends AbstractTaskHandler{

    @Autowired
    private KeyManager keyManager;
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
        if(!(message.getPayload() instanceof ReportStatusUpdRequest)) {
            throw new UnkownMessageException(message);
        }
        //processing message
        ReportStatusUpdRequest req = (ReportStatusUpdRequest)message.getPayload();
        String reportKey = req.getOrigialKey();
        String iptProcKey = req.getSubMessageKey();
        synchronized(this.getClass()) {
            ProcessResult reportResult = this.redisStore.read(reportKey, ProcessResult.class);//if no found?
            ProcessResult iptProcResult = this.redisStore.read(iptProcKey, ProcessResult.class);//if no found?
            
            if(reportResult!=null && ReportStatus.CANCEL.compareTo(reportResult.getStatus())==0) {
                log.info("the original message was cancelled: {}", reportResult.getMessage());
                return;
            }
            if(CollectionUtils.isEmpty(reportResult.getSubMessageResults())) {
                reportResult.setSubMessageResults(new ArrayList<>());
            }
            List<SubMessageResult> subMessageResults = reportResult.getSubMessageResults();
            subMessageResults.add(SubMessageResult.builder().messageKey(iptProcKey).status(iptProcResult.getStatus()).build());
            
            //check all finish?
            int totalMessages = reportResult.getTotalSubMessages();
            long finishCount = subMessageResults.size();
            if(finishCount<totalMessages) {
                log.info("number finished out of total: {}/{} for message: {}", finishCount, totalMessages, reportResult.getMessage());
            }
            else {
                log.info("all sub tasks({}) for this report request are completed: {}", totalMessages, reportResult.getMessage());
                reportResult.setProcessEndTime(LocalDateTime.now());
                reportResult.setStatus(ReportStatus.SUCCESS);
                this.redisStore.write(keyManager.generateQueueKeyByReportKey(reportKey), null);
            }
            
            this.redisStore.write(reportKey, reportResult);
        }
        
    }

}