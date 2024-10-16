package com.example.template.config.rabbitmq;

import com.example.template.common.properties.RabbitMQProperties;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 默认RabbitMQ配置类
 *
 * @author hzh
 */
@Configuration
@AllArgsConstructor
public class DefaultRabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    /**
     * 注册默认交换机
     *
     * @return TopicExchange
     */
    @Bean
    public TopicExchange defaultExchange() {
        return new TopicExchange(rabbitMQProperties.getExchange().getDefaultExchange());
    }

    /**
     * 注册默认队列
     *
     * @return Queue
     */
    @Bean
    public Queue defaultQueue() {
        return new Queue(rabbitMQProperties.getQueue().getDefaultQueue(), true); // 持久化默认队列
    }

    /**
     * 绑定默认队列和交换机
     *
     * @return Binding
     */
    @Bean
    public Binding bindingDefault() {
        return BindingBuilder.bind(defaultQueue())
                .to(defaultExchange())
                .with(rabbitMQProperties.getRoutingKey().getDefaultRoutingKey());
    }
}
