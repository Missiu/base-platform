package com.example.template.manager.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 消息队列消息类
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RabbitMessage {

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息内容
     */
    private Object message;

    /**
     * 时间戳
     */
    private Long timestamp;
}