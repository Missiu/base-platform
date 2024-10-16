package com.example.template.manager.sms;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.example.template.common.base.ErrorCode;
import com.example.template.common.properties.SMSProperties;
import com.example.template.exception.customize.RemoteServiceException;
import com.example.template.manager.sms.condition.SMSCondition;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author hzh
 * @data 2024/10/14 9:19
 */
@Service
@Slf4j
@Conditional(SMSCondition.class)
public class SMSManager {

    @Resource(name = "SMSProperties")
    private SMSProperties smsProperties;
    /**
     * 发送短信的方法。
     *
     * @param phoneNumbers 接收短信的电话号码。
     * @param code 短信参数。
     * @throws Exception 如果程序执行过程中发生错误，抛出异常。
     */
    public void sendSms(String phoneNumbers, String code) {

        // 配置凭证认证信息，包括 accessKeyId、accessKeySecret
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(smsProperties.getAccessKeyId())
                .accessKeySecret(smsProperties.getAccessKeySecret())
                .build());

        // 配置客户端
        AsyncClient client = AsyncClient.builder()
                .region(smsProperties.getRegionId()) // 从配置读取区域ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(smsProperties.getEndpoint()) // 从配置读取 Endpoint
                )
                .build();

        // API 请求参数设置
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(smsProperties.getSignName()) // 从配置读取签名
                .templateCode(smsProperties.getTemplateCode()) // 从配置读取模板代码
                .phoneNumbers(phoneNumbers) // 传入电话号码
                .templateParam("{\"code\":\"" + code + "\"}") // 传入模板参数
                .build();

        // 异步获取 API 请求返回值
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        // 同步获取 API 请求返回值
        try {
        SendSmsResponse resp = response.get();
//        System.out.println(new Gson().toJson(resp));
        }catch (Exception e){
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0501,"验证码发送失败");
        }finally {
            // 最后关闭客户端
            client.close();
        }

    }
}
