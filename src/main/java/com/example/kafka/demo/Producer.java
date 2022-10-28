package com.example.kafka.demo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.example.kafka.demo.entity.Message;
import com.example.kafka.demo.entity.ReportDetailMessage;
import com.example.kafka.demo.entity.ReportMessage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class Producer
{
    //private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, ReportMessage> kafkaTemplate2;
    private final KafkaTemplate<String, ReportDetailMessage> kafkaTemplate3;
    /*
    public void sendMessage(Task t) {
        kafkaTemplate.send("report-in", t.getId()+"", t.toString())
          .addCallback(
            result -> log.info("Message sent to topic: {}", t),
            ex -> log.error("Failed to send message", ex)
          );
    }
    */
    public void sendMessage(Message m)
    {
        kafkaTemplate.send("ia-report", m.getKey(), m.getValue())
        .addCallback(
          result -> log.info("Message sent to topic: {}", m),
          ex -> log.error("Failed to send message", ex)
        );
        
    }
    
    public void sendMessage(ReportMessage m)
    {
        kafkaTemplate2.send("ia-report", m.getKey(), m)
        .addCallback(
          result -> log.info("Message sent to topic: {}", m),
          ex -> log.error("Failed to send message", ex)
        );
        
    }
    /*
     * send message with custom header
     */
    public void sendMessage(ReportDetailMessage m)
    {
        String topic = "ia-report-detail";
        org.springframework.messaging.Message<ReportDetailMessage> message = MessageBuilder
                .withPayload(m)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, m.getKey())
                //.setHeader(KafkaHeaders.PARTITION_ID, 0)
                .setHeader("X-Custom-Header-DataType", ReportDetailMessage.class.getName())
                .build();

        log.info("sending message='{}' to topic='{}'", m, topic);
        kafkaTemplate3.send(message);
        
    }

    public void sendMessage(String key, String value)
    {
        kafkaTemplate.send("ia-report", key, value)
        .addCallback(
          result -> log.info("Message sent to topic: {}", value),
          ex -> log.error("Failed to send message", ex)
        );
        
    }
}
