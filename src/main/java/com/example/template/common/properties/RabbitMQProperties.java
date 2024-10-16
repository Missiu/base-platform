package com.example.template.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ属性配置类
 * 该类包含了RabbitMQ的各种配置属性，包括交换机、队列、路由键的配置以及消息的过期时间
 *
 * @author hzh
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    /**
     * 交换机属性配置
     * 包含了默认交换机、延迟交换机和死信交换机的名称
     */
    private ExchangeProperties exchange;

    /**
     * 队列属性配置
     * 包含了默认队列、延迟队列和死信队列的名称
     */
    private QueueProperties queue;

    /**
     * 路由键属性配置
     * 包含了默认路由键、延迟路由键和死信路由键的名称
     */
    private RoutingKeyProperties routingKey;

    /**
     * 消息的过期时间（Time To Live，TTL）
     * 单位为毫秒，设置后消息如果在指定时间内没有被消费，则会被丢弃或路由到死信交换机
     */
    private long ttl;


    /**
     * 定义交换属性类，用于配置交换机的相关属性
     */
    @Data
    public static class ExchangeProperties {
        /**
         * 默认交换机的名称
         */
        private String defaultExchange;

        /**
         * 延迟交换机的名称
         */
        private String delay;

        /**
         * 死信交换机的名称
         */
        private String dlx;
    }


    /**
     * QueueProperties类用于封装队列的相关属性
     * 它定义了队列的默认名称、延迟队列和死信队列的属性
     */
    @Data
    public static class QueueProperties {
        /**
         * 默认队列的名称
         */
        private String defaultQueue;
        /**
         * 延迟队列的属性，用于指定延迟消息的行为
         */
        private String delay;
        /**
         * 死信队列的属性，用于处理无法被消费的消息
         */
        private String dlx;
    }

    /**
     * RoutingKeyProperties类用于封装路由密钥的相关属性
     * 它定义了消息发布的默认路由密钥、延迟路由密钥和死信路由密钥
     */
    @Data
    public static class RoutingKeyProperties {
        /**
         * 默认路由密钥，用于指定消息发布的默认目标
         */
        private String defaultRoutingKey;
        /**
         * 延迟路由密钥，用于指定延迟消息的路由
         */
        private String delay;
        /**
         * 死信路由密钥，用于指定无法被消费的消息路由
         */
        private String dlx;
    }
}

