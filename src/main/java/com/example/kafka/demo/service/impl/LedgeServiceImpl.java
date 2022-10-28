package com.example.kafka.demo.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.IptTransaction;
import com.example.kafka.demo.entity.LedgePost;
import com.example.kafka.demo.entity.LedgePostType;
import com.example.kafka.demo.service.LedgeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LedgeServiceImpl implements LedgeService
{

    @Override
    public List<LedgePost> getLedagePost(IptTransaction transaction)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LedgePost> getLedagePost(String transactionId)
    {
        log.info("getLedagePost for {}", transactionId);
        return
                Stream.concat(
        IntStream.range(1, 10).mapToObj(x->{
            return LedgePost.builder().transactionId("ipt"+x).postType(LedgePostType.DEBIT).ledgeAccount("cost").amount(BigDecimal.valueOf(1000.0)).build();
        }),
        IntStream.range(1, 10).mapToObj(x->{
            return LedgePost.builder().transactionId("ipt"+x).postType(LedgePostType.CREDIT).ledgeAccount("gl").amount(BigDecimal.valueOf(1000.0)).build();
        }))
       /*return Arrays.asList(
                LedgePost.builder().transactionId("ipt01").postType(LedgePostType.DEBIT).ledgeAccount("cost").amount(BigDecimal.valueOf(1000.0)).build(),
                LedgePost.builder().transactionId("ipt01").postType(LedgePostType.CREDIT).ledgeAccount("gl").amount(BigDecimal.valueOf(100.0)).build(),
                LedgePost.builder().transactionId("ipt02").postType(LedgePostType.DEBIT).ledgeAccount("cost").amount(BigDecimal.valueOf(2000.0)).build(),
                LedgePost.builder().transactionId("ipt02").postType(LedgePostType.CREDIT).ledgeAccount("gl").amount(BigDecimal.valueOf(200.0)).build(),
                LedgePost.builder().transactionId("ipt03").postType(LedgePostType.DEBIT).ledgeAccount("cost").amount(BigDecimal.valueOf(3000.0)).build(),
                LedgePost.builder().transactionId("ipt03").postType(LedgePostType.CREDIT).ledgeAccount("gl").amount(BigDecimal.valueOf(300.0)).build(),
                LedgePost.builder().transactionId("ipt04").postType(LedgePostType.DEBIT).ledgeAccount("cost").amount(BigDecimal.valueOf(4000.0)).build(),
                LedgePost.builder().transactionId("ipt04").postType(LedgePostType.CREDIT).ledgeAccount("gl").amount(BigDecimal.valueOf(400.0)).build()
                )
        .stream()*/
        .filter(x->x.getTransactionId().equals(transactionId))
        .collect(Collectors.toList());
    }
}
