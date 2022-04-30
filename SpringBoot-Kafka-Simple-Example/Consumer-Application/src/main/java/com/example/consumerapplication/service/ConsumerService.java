package com.example.consumerapplication.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @KafkaListener(topics = "wool_kafka_topic")
    public void receive(String message) {

        System.out.println(message);
    }

}
