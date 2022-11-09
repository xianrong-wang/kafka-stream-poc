package com.example.kafka.demo.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.IptTransaction;
import com.example.kafka.demo.service.IptService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IptServiceImpl implements IptService
{

    @Override
    public List<IptTransaction> getIptTransactions(String tenant, LocalDate reportDate)
    {
        try
        {
            log.info("calculating Ipt Transactions");
            TimeUnit.SECONDS.sleep((int)Math.random()*1);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       return IntStream.range(1, 10).mapToObj(x->{
            return IptTransaction.builder().eventId("ipt"+x).fromEntityId("pt"+x).toEntityId("pt"+(x+1)).shares((int)Math.random()*1000).build();
        }).collect(Collectors.toList());
       /*
        * return Arrays.asList( IptTransaction.builder().eventId("ipt01").fromEntityId("pt001").toEntityId("pt002").shares(100).build(),
        * IptTransaction.builder().eventId("ipt02").fromEntityId("pt002").toEntityId("pt003").shares(200).build(),
        * IptTransaction.builder().eventId("ipt03").fromEntityId("pt003").toEntityId("pt004").shares(100).build(),
        * IptTransaction.builder().eventId("ipt04").fromEntityId("pt004").toEntityId("pt005").shares(100).build() );
        */
    }
}
