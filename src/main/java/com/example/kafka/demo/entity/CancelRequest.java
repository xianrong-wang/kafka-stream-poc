package com.example.kafka.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data @AllArgsConstructor
public class CancelRequest
{
    private String messageKey;
}
