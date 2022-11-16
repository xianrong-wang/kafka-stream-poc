package com.example.kafka.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.kafka.demo.entity.ReportMessage;
import com.example.kafka.demo.entity.ReportStatus;
import com.example.kafka.demo.json.StatusDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;

//@SpringBootTest
@Slf4j
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}
	
	
	@Test
	void testSerialization() throws JsonProcessingException {
	    ReportMessage msg = new ReportMessage();
	    msg.setStatus(ReportStatus.NONE);
	   final ObjectMapper objectMapper = new ObjectMapper();
	   SimpleModule module = new SimpleModule();
	   module.addDeserializer(ReportStatus.class, new StatusDeserializer());
	   objectMapper.registerModule(module);
	   log.info( objectMapper.writeValueAsString(msg));
	    
	}
	
	@Test
    void testDeserialization() throws JsonProcessingException {
        /*
         * ReportMessage msg = new ReportMessage(); msg.setStatus(ReportStatus.NONE);
         */
       final ObjectMapper objectMapper = new ObjectMapper();
       SimpleModule module = new SimpleModule();
       module.addDeserializer(ReportStatus.class, new StatusDeserializer());
       objectMapper.registerModule(module);
       String msg = "{\"key\":\"rpt06\",\"tenant\":null,\"name\":\"report test 01\",\"reportDate\":\"20221022\","
               + "\"count\":0,\"createdTimestamp\":0,\"updatedTimestamp\":0,\"overwrite\":false}";
       log.info( "{}",objectMapper.readValue(msg,ReportMessage.class));
       
      
        
    }
	
	@Test
	void doTest() throws JsonMappingException, JsonProcessingException {
	    ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
	    Mockito.doThrow(JsonProcessingException.class).when(objectMapper).readValue("abc", String.class);
	    Assertions.assertThrows(JsonProcessingException.class,()->{
	        objectMapper.readValue("abc", String.class);
	    });
	    
	}
	
}
