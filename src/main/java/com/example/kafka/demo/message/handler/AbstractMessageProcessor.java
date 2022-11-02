package com.example.kafka.demo.message.handler;

import com.example.kafka.demo.service.Cancelable;
import com.example.kafka.demo.service.Overwritable;

abstract class AbstractMessageProcessor implements MessageProcessor, Overwritable, Cancelable{
    
}