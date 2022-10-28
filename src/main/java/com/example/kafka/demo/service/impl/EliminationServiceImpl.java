package com.example.kafka.demo.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.LedgePost;
import com.example.kafka.demo.service.EliminationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EliminationServiceImpl implements EliminationService
{

    @Override
    public List<LedgePost> generateElimLedgePost(List<LedgePost> posts)
    {
        log.info("generateElimLedgePost for {}", posts);
        List<LedgePost> results = new ArrayList<>();
        Optional<LedgePost> glPostOpt = posts.stream().filter(p->p.getLedgeAccount().equals("gl")).findFirst();
        if(glPostOpt.isPresent()) {
            final LedgePost glPost = glPostOpt.get();
            //fix gl
            results.add(LedgePost.builder()
                    .entityId(this.getElimEntity(glPost.getTransactionId()))
                    .ledgeAccount(glPost.getLedgeAccount())
                    .amount(glPost.getAmount().multiply(BigDecimal.valueOf(-1)))
                    .postType(glPost.getPostType())
                    .build());
            //fix cost
            Optional<LedgePost> costPostOpt =  posts.stream().filter(p->p.getLedgeAccount().equals("cost")).findFirst();
            if(costPostOpt.isPresent()) {
                final LedgePost costPost = costPostOpt.get();
                results.add(LedgePost.builder()
                        .entityId(this.getElimEntity(costPost.getTransactionId()))
                        .ledgeAccount(costPost.getLedgeAccount())
                        .amount(glPost.getAmount().multiply(BigDecimal.valueOf(-1)))
                        .postType(costPost.getPostType())
                        .build());
            }
            
        }
        return results;
    }
    /*
     * get fix entity based on company matrix
     */
    private String getElimEntity(String transactionId) {
        return "FX_"+transactionId;
    }
}
