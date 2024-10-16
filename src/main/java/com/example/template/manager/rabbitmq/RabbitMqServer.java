package com.example.template.manager.rabbitmq;

import com.example.template.common.properties.RabbitMQProperties;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author hzh
 * @data 2024/10/21 10:58
 */

@Service
@AllArgsConstructor
@Slf4j
public class RabbitMqServer {

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    private final RabbitMQProperties rabbitMQProperties;

    /**
     * 向默认的不带有死信队列的消息队列发送消息
     *
     * @param message 消息体
     */
    public void send(Object message) {
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange().getDefaultExchange(),
                rabbitMQProperties.getRoutingKey().getDefaultRoutingKey(),
                message);
    }

    /**
     * 消费者接受消息
     */
    public void receive() {
        rabbitTemplate.receiveAndConvert(rabbitMQProperties.getQueue().getDefaultQueue());
    }


    /**
     * 生成默认messageId
     */
    private String generateMessageId() {
        return UUID.randomUUID().toString();
    }
}
