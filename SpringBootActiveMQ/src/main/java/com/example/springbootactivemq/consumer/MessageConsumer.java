package com.example.springbootactivemq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @JmsListener(destination = "test-queue")
    public void receiveMessage(String message) {
        logger.info("Received message: {}", message);
    }

    @JmsListener(destination = "test-queue")
    @SendTo("greet-queue")
    public String receiveMessageAndReply(String message) {
        logger.info("Received message: {}", message);
        return "Hello " + message;
    }

    @JmsListener(destination = "greet-queue")
    public void receiveGreeting(String message) {
        logger.info("Received greeting: {}", message);
    }
}
