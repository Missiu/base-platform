package com.example.template.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.template.common.base.CommonConstants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
@Configuration
@Slf4j
public class MyBatisPlusConfiguration {

    /**
     * MyBatis-Plus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 防止全表更新与删除插件
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 数据变动记录插件
        mybatisPlusInterceptor.addInnerInterceptor(new DataChangeRecorderInnerInterceptor());
        // 分页插件，如果有多个插件，分页插件添加在最后
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInnerInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(CommonConstants.MAX_PAGE_SIZE);
        // 开启分页插件
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        return mybatisPlusInterceptor;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initConfig() {
        log.info("=====> {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}