package com.example.template.manager.sms.condition;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 判断是否开启Caffeine本地缓存
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
public class SMSCondition implements Condition {


    /**
     * 判断是否开启Caffeine本地缓存
     *
     * @param context  上下文
     * @param metadata 元数据
     * @return 是否开启
     */
    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("sms.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }

}