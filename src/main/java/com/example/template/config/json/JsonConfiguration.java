package com.example.template.config.json;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * SpringMVC JSON 配置
 *
 * @author AntonyCheng
 */
@JsonComponent
@Slf4j
public class JsonConfiguration {

    /**
     * 添加 Long 转 json 精度丢失的配置
     *
     * @return Json自定义处理器
     */
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
            // 定义日期时间格式化器
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 为 LocalDateTime 类型添加自定义序列化器，使用定义好的格式化器
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            // 为 LocalDateTime 类型添加自定义反序列化器，使用定义好的格式化器
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            // 将自定义模块添加到 builder 中
            builder.modules(javaTimeModule);
            // 设置时区为默认时区
            builder.timeZone(TimeZone.getDefault());
        };
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("===== {} Configuration DI. =====", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}