package com.example.kafka.demo.service;

import java.util.concurrent.Future;

public interface TaskManager
{
    Future<?> run(Runnable run,String taskId);
   void cancel(String taskId);
}
