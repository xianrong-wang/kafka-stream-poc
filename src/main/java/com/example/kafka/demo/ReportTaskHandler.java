package com.example.kafka.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.IptTransaction;
import com.example.kafka.demo.entity.LedgePost;
import com.example.kafka.demo.entity.ReportDetailMessage;
import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.entity.ReportReqResult;
import com.example.kafka.demo.entity.ReportRequest;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.redisks.RedisStore;
import com.example.kafka.demo.service.EliminationService;
import com.example.kafka.demo.service.IptService;
import com.example.kafka.demo.service.LedgeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ReportTaskHandler
{
    final private IptService iptServ;
    final private EliminationService elimServ;
    final private LedgeService ledgeServ;
    final private Producer producer;
    final private RedisClient<String,ReportReqResult> redisClient;
    
    public ReportReqResult process(ReportMessage msg, RedisStore<String,ReportReqResult> redisStore) throws InterruptedException{
        List<String> trackDetailTasks = new ArrayList<>();
        List<String> trackDetailTasksFinish = new ArrayList<>();
        List<ReportReqResult> subResults = new ArrayList<>();
        ReportReqResult exResult = redisStore.read(msg.getKey(), ReportReqResult.class);
        ReportReqResult result = null;
        Map<String,ReportReqResult> mapResult = new HashMap<>();
        if(exResult!=null && (ReportStatus.SUCCESS.compareTo(exResult.getStatus())==0 || ReportStatus.FAIL.compareTo(exResult.getStatus())==0)){
            log.info("this request is already completed: {}", msg);
            return exResult;
        }
        if(exResult!=null && ReportStatus.PROCESSING.compareTo(exResult.getStatus())==0) {
            //inprogress
            //monitor the progress
            exResult
            .getSubReqResult()
            .forEach(x->{
                trackDetailTasks.add(x.getReportRequest().getIptTransactionId());
                mapResult.put(x.getReportRequest().getIptTransactionId(), x);
            });
            result = exResult;
        }
        if(exResult==null) {
            //new
            result = ReportReqResult.builder()
                    .reportRequest(
                            ReportRequest.builder()
                            .user(null)
                            .tenant(null)
                            .reportDate(LocalDate.parse(msg.getReportDate(), DateTimeFormatter.ofPattern("yyyyMMdd"))).build())
                    .status(ReportStatus.PROCESSING).startTime(LocalDateTime.now())
                    .subReqResult(subResults)
                    .build();
            
            iptServ.getIptTransactions(msg.getTenant(), msg.getReportDate())
            .parallelStream().peek(x->log.info("ipt: {}", x))
            .map(x->this.buildReportDetailMessage(x,msg))
            .forEach(x->{
                final ReportReqResult subResult = ReportReqResult.builder()
                        .reportRequest(
                                ReportRequest.builder()
                                .user(null)
                                .tenant(null)
                                .iptTransactionId(x.getTransactionId())
                                .reportDate(LocalDate.parse(msg.getReportDate(), DateTimeFormatter.ofPattern("yyyyMMdd"))).build())
                        .status(ReportStatus.NONE).build();
                subResults.add(subResult);
                trackDetailTasks.add(x.getKey());
                mapResult.put(x.getKey(), subResult);
                producer.sendMessage(x);})
            ;
            redisStore.write(msg.getKey(), result);
        }
        
        //monitor all sub tasks status
        long timeoutInMinutes = 120;
        long start = System.currentTimeMillis();
        while(trackDetailTasks.size()>trackDetailTasksFinish.size()) {
            trackDetailTasks.stream().filter(t->!trackDetailTasksFinish.contains(t))
            .forEach(t->{
                log.info("checking task: {}", t);
                ReportReqResult detailResult = redisClient.read(t, ReportReqResult.class);
                if(detailResult!=null && (ReportStatus.PROCESSING.compareTo(detailResult.getStatus())!=0 && ReportStatus.NONE.compareTo(detailResult.getStatus())!=0)) {
                    ReportReqResult reportReqResult = mapResult.get(t);
                    if(reportReqResult!=null) {
                        reportReqResult.setStartTime(detailResult.getStartTime());
                        reportReqResult.setEndTime(detailResult.getEndTime());
                        reportReqResult.setStatus(reportReqResult.getStatus());
                        reportReqResult.setMessages(reportReqResult.getMessages());
                    }
                    log.info("sub task is completed: {}", t);
                    trackDetailTasksFinish.add(t);
                }
            });
          
          if(trackDetailTasks.size()==trackDetailTasksFinish.size()) {
              break;
          }
          
          TimeUnit.SECONDS.sleep(10);
          
          long end = System.currentTimeMillis();
          if(end - start > timeoutInMinutes*60*1000) {
              String error = String.format("timeout to process the message: %s", msg);
              log.error(error);
              result.setStatus(ReportStatus.FAIL);
              result.setMessages(Arrays.asList(error));
              result.setEndTime(LocalDateTime.now());
              redisStore.write(msg.getKey(), result);
              return result;
          }
        }
        
        result.setStatus(ReportStatus.SUCCESS);
        result.setEndTime(LocalDateTime.now());
        redisStore.write(msg.getKey(), result);
        return result;
    }
    
    public ReportReqResult processReportDetail(ReportDetailMessage msg,RedisStore<String,ReportReqResult> redisStore) {
        log.info("processing sub message: {}", msg);
        ReportReqResult result = ReportReqResult.builder()
        .reportRequest(
                ReportRequest.builder()
                .user(null)
                .tenant(null)
                .iptTransactionId(msg.getTransactionId())
                //.reportDate(LocalDate.parse(msg.getReportDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build())
        .status(ReportStatus.PROCESSING)
        .startTime(LocalDateTime.now()).build();
        redisStore.write(msg.getKey(), result);
        
        List<LedgePost> posts = elimServ.generateElimLedgePost(ledgeServ
                .getLedagePost(msg.getTransactionId()));
       //sink into db
        log.info("persist elimination entries: {}", posts);
        msg.setStatus(ReportStatus.SUCCESS);
        result.setEndTime(LocalDateTime.now());
        result.setStatus(ReportStatus.SUCCESS);
        redisStore.write(msg.getKey(), result);
        return result;
    }
    /*
     * build report detail message based on ipt transaction
     */
    private ReportDetailMessage buildReportDetailMessage(IptTransaction ipt,ReportMessage msg) {
        return ReportDetailMessage.builder().key(ipt.getEventId()).transactionId(ipt.getEventId()).reportDate(msg.getReportDate()).build();
    }
}
