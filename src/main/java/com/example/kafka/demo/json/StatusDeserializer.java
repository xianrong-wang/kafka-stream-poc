package com.example.kafka.demo.json;

import java.io.IOException;
import java.util.stream.Stream;

import com.example.kafka.demo.entity.ReportStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class StatusDeserializer extends JsonDeserializer<ReportStatus>
{
    @Override
    public ReportStatus deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAsTree();
        if(node==null) return ReportStatus.NONE;
        /*
         * if (node==null || node.asText().isEmpty()) { return ReportStatus.NONE; }
         */
        return Stream.of(
                ReportStatus.values())
                .filter(s->s.name().equals(node.asText()))
                //.peek(x->log.info("{}",x))
                .findFirst()
                .orElseGet(()->ReportStatus.NONE);
    }


}
