package com.example.kafka.demo.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.example.kafka.demo.entity.IptRequest;
import com.example.kafka.demo.entity.ReportRequest;


@Service
public class KeyManager
{

    private static final String SEPARATOR = "$";
    private static final String REGEX_SPLIT = "\\"+SEPARATOR;

    public String generateReportKey(ReportRequest rtpRequest)
    {
        return String.join( SEPARATOR,rtpRequest.getTenant(), "Report",
                rtpRequest.getReportDate().format(DateTimeFormatter.ISO_DATE),
                rtpRequest.getRequestDatetime().format(DateTimeFormatter.ISO_DATE_TIME)
                );
    }
    
    public String generateIptKey(IptRequest iptRequest)
    {
        return String.join( SEPARATOR,
                buildIptPrefix(
                iptRequest.getTenant(),
                iptRequest.getRequestDatetime().format(DateTimeFormatter.ISO_DATE_TIME)),
                iptRequest.getTransactionId()
                );
    }
    
    public String generateStatusKey(IptRequest iptRequest)
    {
        return String.join(SEPARATOR,iptRequest.getTenant(), generateIptKey(iptRequest), "Status");
    }
    
    public String generateQueueKey(String tenant)
    {
        return "Queue"+SEPARATOR+tenant;
    }

    public String generateQueueKeyByReportKey(String reportKey)
    {
        return generateQueueKey(reportKey.split(REGEX_SPLIT)[1]);
    }

    public String getIptKeyPrefixReportKey(String reportKey)
    {
        final String[] split = reportKey.split(REGEX_SPLIT);
        String requestTime = split[3];
        String tenant = split[0];
        return buildIptPrefix(
                tenant,
                requestTime);
    }
    
    private String buildIptPrefix(String tenant,String requestTime) {
        return String.join( SEPARATOR,
                tenant,
                "IPT", 
                requestTime);
    }

    public String getTenantFromKey(String key)
    {
        return key.split(REGEX_SPLIT)[0];
    }
}
