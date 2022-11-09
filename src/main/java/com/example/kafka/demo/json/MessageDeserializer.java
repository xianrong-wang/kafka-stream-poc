package com.example.kafka.demo.json;

import java.io.IOException;

import com.example.kafka.demo.entity.Message;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class MessageDeserializer<T> extends JsonDeserializer<Message<T>>
{
    @Override
    public Message<T> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAsTree();
        if(node==null) return null;
        Message<T> m = new  Message<T>();
        m.setKey(node.get("key").asText());
        ObjectMapper om = new ObjectMapper();
        m.setPayload(
        (T) om.readValue(om.writeValueAsString(node.get("payload")), m.getPayload().getClass())
        );
        return m;
    }


}
