package com.example.kafka.demo.redis.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.kafka.demo.entity.ProcessResult;

@Repository
public interface ReportStatusRepo extends CrudRepository<ProcessResult, String> 
{
    
}
