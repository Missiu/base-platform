package com.example.template.config.json;

import com.example.template.common.base.CommonConstants;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * SpringMVC JSON 配置
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
@JsonComponent
@Slf4j
public class JsonConfig {

    /**
     * 配置 Jackson 处理器，优化 JSON 序列化和反序列化过程
     *
     * @return 返回一个定制化的 Jackson2ObjectMapperBuilderCustomizer 实例
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            // 全局配置序列化返回 JSON 处理
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            // 添加自定义序列化器，处理大数字
            javaTimeModule.addSerializer(Long.class, BigNumberSerializer.instance);
            javaTimeModule.addSerializer(Long.TYPE, BigNumberSerializer.instance);
            javaTimeModule.addSerializer(BigInteger.class, BigNumberSerializer.instance);
            javaTimeModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
            // 定义日期时间格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            // 将自定义模块添加到 builder 中
            builder.modules(javaTimeModule);
            // 设置时区为默认时区
            builder.timeZone(TimeZone.getDefault());
        };
    }

    /**
     * 配置 Jackson2JsonMessageConverter，用于处理消息转换
     *
     * @return 返回一个 Jackson2JsonMessageConverter 实例，用于处理消息转换
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}