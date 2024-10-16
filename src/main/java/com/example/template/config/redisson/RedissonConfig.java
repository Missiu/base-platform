package com.example.template.config.redisson;

import com.example.template.common.properties.RedissonProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类，提供 Redis 连接客户端。
 * 支持单机模式和集群模式（可以通过配置切换）。
 *
 * @author hzh
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@AllArgsConstructor
@Slf4j
public class RedissonConfig {

    private final RedissonProperties redissonProperties;
    private final ObjectMapper objectMapper;

    /**
     * 配置 Redisson 单机模式客户端
     *
     * @return 单机模式的 RedissonClient 实例
     */
    @Bean(name = "redissonClient")
    public RedissonClient singleClient() {
        Config config = new Config();
        config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setCodec(new JsonJacksonCodec(objectMapper));

        RedissonProperties.SingleServerConfig singleConfig = redissonProperties.getSingleServerConfig();
        config.useSingleServer()
                .setAddress(singleConfig.getAddress())
                .setDatabase(singleConfig.getDatabase())
                .setPassword(singleConfig.getPassword())
                .setTimeout(singleConfig.getTimeout())
                .setIdleConnectionTimeout(singleConfig.getIdleConnectionTimeout())
                .setSubscriptionConnectionPoolSize(singleConfig.getSubscriptionConnectionPoolSize())
                .setConnectionMinimumIdleSize(singleConfig.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(singleConfig.getConnectionPoolSize());
        return Redisson.create(config);
    }

    /**
     * 配置 Redisson 集群模式客户端
     *
     * @return 集群模式的 RedissonClient 实例
     */
//    @Bean(name = "redissonClient")
//    @Conditional(RedissonClusterCondition.class)
//    public RedissonClient clusterClient() {
//        Config config = new Config();
//        config.setThreads(redissonProperties.getThreads())
//                .setNettyThreads(redissonProperties.getNettyThreads())
//                .setCodec(new JsonJacksonCodec(objectMapper));
//
//        RedissonProperties.ClusterServersConfig clusterConfig = redissonProperties.getClusterServersConfig();
//        config.useClusterServers()
//                .setPassword(clusterConfig.getPassword())
//                .setMasterConnectionMinimumIdleSize(clusterConfig.getMasterConnectionMinimumIdleSize())
//                .setMasterConnectionPoolSize(clusterConfig.getMasterConnectionPoolSize())
//                .setSlaveConnectionMinimumIdleSize(clusterConfig.getSlaveConnectionMinimumIdleSize())
//                .setSlaveConnectionPoolSize(clusterConfig.getSlaveConnectionPoolSize())
//                .setIdleConnectionTimeout(clusterConfig.getIdleConnectionTimeout())
//                .setTimeout(clusterConfig.getTimeout())
//                .setSubscriptionConnectionPoolSize(clusterConfig.getSubscriptionConnectionPoolSize())
//                .setReadMode(ReadMode.SLAVE)
//                .setSubscriptionMode(SubscriptionMode.MASTER)
//                .setNodeAddresses(clusterConfig.getNodeAddresses());
//
//        log.info("Redisson cluster mode client configured for nodes: {}", clusterConfig.getNodeAddresses());
//        return Redisson.create(config);
//    }

    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
