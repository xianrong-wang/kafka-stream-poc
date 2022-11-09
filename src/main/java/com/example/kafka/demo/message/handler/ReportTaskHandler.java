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
import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.entity.SubMessageResult;
import com.example.kafka.demo.service.IptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
@Slf4j
@AllArgsConstructor
@Component
public class ReportTaskHandler extends AbstractTaskHandler{

    final private IptService iptServ;
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
        if(!(message.getPayload() instanceof ReportRequest)) {
            throw new UnkownMessageException(message);
        }
        //processing message
        ReportRequest req = (ReportRequest)message.getPayload();
        //check the existing result
        ProcessResult exResult = this.getRedisStore().read(message.getKey(), ProcessResult.class);
        if(exResult!=null && (ReportStatus.SUCCESS.compareTo(exResult.getStatus())==0 && !req.isOverwrite())) {
            return;
        }
        ProcessResult result = ProcessResult.builder().message(message).processStartTime(LocalDateTime.now()).status(ReportStatus.PROCESSING)
                .subMessageResults(new ArrayList<>())
                .build();
        this.redisStore.write(message.getKey(), result);
        
        if(req.isOverwrite()) {
            cleanup();
        }
        //generate IptTransProcessMessage
        final List<IptTransaction> iptTransactions = iptServ.getIptTransactions(req.getTenant(), req.getReportDate());
        result.setTotalSubMessages(iptTransactions.size());
        redisStore.write(message.getKey(), result);
        
        try
        {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e)
        {
           e.printStackTrace();
        }
        
        iptTransactions
        .parallelStream()
        .peek(x->log.info("ipt: {}", x))
        .map(x->this.IptTransProcessMessage(x,message))
        .forEach(x->{
            producer.sendMessage(x);
            //result.getSubMessageResults().add(SubMessageResult.builder().messageKey(x.getKey()).build());
            })
        ;
    }
    
    /*
     * build report detail message based on ipt transaction
     */
    private Message<IptRequest> IptTransProcessMessage(IptTransaction ipt,Message<?> msg) {
       
        Message<IptRequest> newMessage = new Message<>();
        newMessage.setPayload(
        IptRequest.builder().transactionId(ipt.getEventId()).origReqKey(msg.getKey())
        .requestDatetime( ((ReportRequest)msg.getPayload()).getRequestDatetime()).build()
        );
        newMessage.setKey(generateMessageKey(newMessage.getPayload()));
        return newMessage;
    }
    
    private String generateMessageKey(IptRequest req)
    {
        return String.join( ":","IPT", 
                req.getRequestDatetime().format(DateTimeFormatter.ISO_DATE_TIME),
                req.getTransactionId()
                );
    }
    
}