package com.example.kafka.demo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.kafka.demo.RedisClient;
import com.example.kafka.demo.entity.ProcessResult;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.entity.SubMessageResult;
import com.example.kafka.demo.service.KeyManager;
import com.example.kafka.demo.service.StatusAggregator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StatusAggregatorImpl implements StatusAggregator
{
    
    final RedisClient redis;
    final KeyManager km;
    @Override
    public ProcessResult getProcessResult(String reportKey)
    {
        ProcessResult result = redis.read(reportKey, ProcessResult.class);
        if(result!=null) {
            if(result.getStatus() != ReportStatus.PENDING && result.getStatus() != ReportStatus.PROCESSING) {
                return result;
            }
            else {
                //check all ipt status
               String prefix =  km.getIptKeyPrefixReportKey(reportKey);
               LocalDateTime endtime = result.getProcessEndTime()==null?LocalDateTime.MIN:result.getProcessEndTime();
               redis.readKeys(prefix+"*").forEach(
                       key->{
                           ProcessResult iptResult = redis.read(key, ProcessResult.class);
                           if(iptResult!=null) {
                              if(iptResult.getStatus()==ReportStatus.SUCCESS || iptResult.getStatus()==ReportStatus.FAIL || iptResult.getStatus()==ReportStatus.CANCEL) {
                                 List<SubMessageResult> subResults = result.getSubMessageResults();
                                 if(CollectionUtils.isEmpty(subResults)) {
                                     subResults = new ArrayList<>();
                                     result.setSubMessageResults(subResults); 
                                 }
                                 subResults.add(SubMessageResult.builder().messageKey(iptResult.getMessage().getKey()).status(iptResult.getStatus()).build()) ; 
                                 
                                 if(iptResult.getProcessEndTime().isAfter(endtime)) {
                                     result.setProcessEndTime(iptResult.getProcessEndTime());
                                 }
                              }
                          }
                       }
                       );
            }
            List<SubMessageResult> subResults = result.getSubMessageResults();
            
            if(subResults.size()==result.getTotalSubMessages()) {
                if(subResults.stream().filter(x->x.getStatus()==ReportStatus.FAIL).count()>0) {
                    result.setStatus(ReportStatus.FAIL); 
                }
                else if(subResults.stream().filter(x->x.getStatus()==ReportStatus.CANCEL).count()>0) {
                    result.setStatus(ReportStatus.CANCEL); 
                }
                else {
                    result.setStatus(ReportStatus.SUCCESS); 
                }
                redis.write(reportKey, result);
            }
        }
        
        
        return result;
    }

}
