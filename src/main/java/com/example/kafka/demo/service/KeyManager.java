package com.example.kafka.demo.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.ReportRequest;


@Service
public class KeyManager
{

    private static final String SEPARATOR = ":";

    public String generateReportKey(ReportRequest rtpRequest)
    {
        return String.join( SEPARATOR,"Report",rtpRequest.getTenant(), 
                rtpRequest.getReportDate().format(DateTimeFormatter.ISO_DATE),
                rtpRequest.getRequestDatetime().format(DateTimeFormatter.ISO_DATE_TIME)
                );
    }
    
    public String generateIptKey(IptRequest iptRequest)
    {
        return String.join( SEPARATOR,"IPT", 
                iptRequest.getRequestDatetime().format(DateTimeFormatter.ISO_DATE_TIME),
                iptRequest.getTransactionId()
                );
    }
    
    public String generateStatusKey(IptRequest iptRequest)
    {
        return generateIptKey(iptRequest)+":Status";
    }
    
    public String generateQueueKey(String tenant)
    {
        return "Queue:"+tenant;
    }

    public String generateQueueKeyByReportKey(String reportKey)
    {
        return generateQueueKey(reportKey.split(SEPARATOR)[1]);
    }
}
