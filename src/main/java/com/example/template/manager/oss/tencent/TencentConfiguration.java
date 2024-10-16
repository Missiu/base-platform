package com.example.template.manager.oss.tencent;

import com.example.template.common.base.ErrorCode;
import com.example.template.exception.customize.RemoteServiceException;
import com.example.template.manager.oss.tencent.properties.TencentProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS连接配置类
 * 提供COSClient的配置和创建方法
 * 
 * @Author: AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(TencentProperties.class)
@AllArgsConstructor
@Slf4j
public class TencentConfiguration {

    private final TencentProperties tencentProperties;

    /**
     * 创建COSClient实例
     * 
     * @return COSClient
     */
    public COSClient createCosClient() {
        try {
            COSCredentials credentials = new BasicCOSCredentials(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
            Region region = new Region(tencentProperties.getRegion());
            ClientConfig clientConfig = new ClientConfig(region);
            clientConfig.setHttpProtocol(HttpProtocol.https);
            return new COSClient(credentials, clientConfig);
        } catch (Exception e) {
            log.error("创建COSClient失败: {}", e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0001, e.getMessage());
        }
    }

    /**
     * 初始化DI
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName());
    }
}
