package com.example.template;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TemplateApplicationTests {

	@Autowired
	private DruidDataSource dataSource;


	@Autowired
	private RedissonClient singleClient;

	@Autowired
	private Cache<String, Object> localCache;
	@SneakyThrows
	@Test
	void contextLoads() {
		// 获取Druid的配置信息
		System.out.println("Druid URL: " + dataSource.getUrl());
		System.out.println("Druid Username: " + dataSource.getUsername());
		System.out.println("Druid Initial Size: " + dataSource.getInitialSize());
		System.out.println("Druid Max Active: " + dataSource.getMaxActive());
		System.out.println("Druid Min Idle: " + dataSource.getMinIdle());
		System.out.println("Druid Max Wait: " + dataSource.getMaxWait());
		System.out.println("Druid Time Between Eviction Runs Millis: " + dataSource.getTimeBetweenEvictionRunsMillis());
		System.out.println("Druid Min Evictable Idle Time Millis: " + dataSource.getMinEvictableIdleTimeMillis());

		singleClient.getBucket("test").set("testValue");
	}

}
