package com.example.kafka.demo.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * Configuration for Kafka Streams.
 */
@Configuration
public class KafkaStreamsConfig {
 public static final String CANCEL_STREAMS_BUILDER_BEAN_NAME = "cancelKafkaStreamsBuilder";
 public static final String GENERAL_STREAMS_BUILDER_BEAN_NAME = "generalKafkaStreamsBuilder";
 
 @Value("${spring.kafka.bootstrap-servers}")
 private String bootstrapServers;
 
 @Value("${spring.kafka.streams.properties[num.stream.threads]:1}")
 private int threads;
 
 @Value("${spring.kafka.streams.application-id}")
 private String appId;
 
 @Value("${spring.kafka.streams.properties[state.dir]}")
 private String dir;
 
@Bean(name = GENERAL_STREAMS_BUILDER_BEAN_NAME)
public StreamsBuilderFactoryBean app1KafkaStreamsBuilder() {
    Map<String, Object> generalStreamsConfigProperties = commonStreamsConfigProperties();
    generalStreamsConfigProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "IA_SERVICE");
    return new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(generalStreamsConfigProperties));
}
@Bean(name = CANCEL_STREAMS_BUILDER_BEAN_NAME)
public StreamsBuilderFactoryBean app2KafkaStreamsBuilder() {
    Map<String, Object> cancelStreamsConfigProperties = commonStreamsConfigProperties();
    cancelStreamsConfigProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "IA_SERVICE_CANCEL@"+LocalDateTime.now().getNano());
    cancelStreamsConfigProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");//always consume the latest message
    cancelStreamsConfigProperties.put(StreamsConfig.STATE_DIR_CONFIG, dir);
    return new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(cancelStreamsConfigProperties));
}
private Map<String, Object> commonStreamsConfigProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, threads);
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
    props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getName());
    props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, DefaultProductionExceptionHandler.class.getName());
    props.put(StreamsConfig.STATE_DIR_CONFIG, dir);
    return props;
}

@Bean
public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
      bootstrapServers);
    props.put(
      ConsumerConfig.GROUP_ID_CONFIG, 
      "app_test");
    props.put(
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
      StringDeserializer.class);
    props.put(
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
      StringDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props);
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> 
  kafkaListenerContainerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
      new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
}
}


