package com.example.kafka.demo.processor;

public class ConsolidationException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public ConsolidationException(Exception ex)
    {
        super(ex);
    }

}
