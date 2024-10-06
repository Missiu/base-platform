package com.example.template.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动字段填充器
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
@Component
@Slf4j
public class FieldMetaObjectConfiguration implements MetaObjectHandler {

    /**
     * gmtModified 更新时间字段名
     */
    public static final String MODIFY = "gmtModified";

    /**
     * gmtCreate 创建时间字段名
     */
    public static final String CREATE = "gmtCreate";

    /**
     * 插入时自动填充的字段
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(MODIFY)) {
            metaObject.setValue(MODIFY, LocalDateTime.now());
        }

        if (metaObject.hasSetter(CREATE)) {
            metaObject.setValue(CREATE, LocalDateTime.now());
        }
    }

    /**
     * 更新时自动填充的字段
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(MODIFY)) {
            metaObject.setValue(MODIFY, LocalDateTime.now());
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("=====> {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}