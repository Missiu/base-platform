package com.example.template.config.redisson;

import com.example.template.common.properties.RedissonProperties;
import com.example.template.config.redisson.condition.RedissonClusterCondition;
import com.example.template.config.redisson.condition.RedissonSingleCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * Redisson配置
 *
 * @author hzh
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@AllArgsConstructor
@Slf4j
public class RedissonConfiguration {

    private final RedissonProperties redissonProperties;

    private final ObjectMapper objectMapper;

    /**
     * 创建并配置Redisson单机客户端
     * 该方法用于在单机模式下配置和创建Redisson客户端它根据RedissonProperties配置类中的属性
     * 来设置客户端的各种参数，包括线程数、编解码器、数据库配置等
     *
     * @return 配置好的RedissonClient实例
     */
    @Bean
    @Conditional(RedissonSingleCondition.class)
    public RedissonClient singleClient() {
        // 创建Redisson配置实例
        Config config = new Config();
        // 设置线程数、Netty线程数和编解码器
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setCodec(new JsonJacksonCodec(objectMapper));
        // 获取单机服务器配置
        RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
        // 使用单机模式
        config.useSingleServer()
                // 设置Redis服务器的地址
                .setAddress(singleServerConfig.getAddress())
                // 设置Redis数据库的索引
                .setDatabase(singleServerConfig.getDatabase())
                // 设置访问Redis服务器的密码
                .setPassword(singleServerConfig.getPassword())
                // 设置操作Redis服务器的超时时间
                .setTimeout(singleServerConfig.getTimeout())
                // 设置Redis连接的空闲超时时间
                .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                // 设置订阅连接池的大小
                .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                // 设置连接池中最小的空闲连接数
                .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                // 设置连接池的大小
                .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
        // 根据配置创建并返回Redisson客户端
        return Redisson.create(config);
    }

    /**
     * 创建并配置Redisson集群客户端
     * 仅当不存在名为"RedissonSingleClient"的Bean时，才创建该Bean
     *
     * @return 配置好的RedissonClient实例
     */

    @Bean
    @Conditional(RedissonClusterCondition.class)
    public RedissonClient clusterClient() {
        // 初始化Redisson配置对象
        Config config = new Config();
        // 设置线程数、Netty线程数和序列化方式
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setCodec(new JsonJacksonCodec(objectMapper));
        // 获取集群服务器配置
        RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
        // 使用集群模式配置Redisson
        config.useClusterServers()
                // 设置密码
                .setPassword(clusterServersConfig.getPassword())
                // 设置主节点连接最小空闲数
                .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                // 设置主节点连接池大小
                .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                // 设置从节点连接最小空闲数
                .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                // 设置从节点连接池大小
                .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                // 设置连接空闲超时
                .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                // 设置操作超时
                .setTimeout(clusterServersConfig.getTimeout())
                // 设置订阅连接池大小
                .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                // 设置读模式为从节点
                .setReadMode(ReadMode.SLAVE)
                // 设置订阅模式为主节点
                .setSubscriptionMode(SubscriptionMode.MASTER)
                // 设置节点地址列表
                .setNodeAddresses(clusterServersConfig.getNodeAddresses());
        // 根据配置创建Redisson客户端并返回
        return Redisson.create(config);
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initConfig() {
        log.info("=====> {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}