package com.example.template.manager.cache;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageProducerTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testSendMessage() {
        String queueName = "sample.queue";
        String message = "Hello World";

        rabbitTemplate.convertAndSend("Hello World");
    }
}