package com.example.template.manager.oss.ali;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.example.template.manager.oss.ali.properties.AliProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * OSS 客户端工具类，负责管理 OSS 客户端的创建和关闭
 */
@Slf4j
public class OssClientUtil {

    /**
     * 获取 OSS 客户端
     *
     * @param aliProperties 阿里云配置
     * @return OSS 客户端实例
     */
    public static OSS getOssClient(AliProperties aliProperties) {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(aliProperties.getSecretId(), aliProperties.getSecretKey());
        String endpoint = aliProperties.getEndpoint();
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setProtocol(Protocol.HTTPS);

        log.info("初始化 OSS 客户端，Endpoint: {}", endpoint);
        return new OSSClientBuilder().build(endpoint, credentialsProvider, conf);
    }

    /**
     * 关闭 OSS 客户端
     *
     * @param ossClient OSS 客户端实例
     */
    public static void shutdownOssClient(OSS ossClient) {
        if (ossClient != null) {
            log.info("关闭 OSS 客户端");
            ossClient.shutdown();
        }
    }
}
