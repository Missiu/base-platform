package com.example.template.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import com.example.template.common.base.ErrorCode;
import com.example.template.exception.customize.ServiceException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * SaToken认证配置
 * 用于配置SaToken的拦截器和过滤器
 *
 * @author hzh
 */
@Configuration
@Slf4j
public class SaTokenAuthConfig implements WebMvcConfigurer {

    /**
     * 定义SaToken不需要拦截的URI
     */
    private static final List<String> SA_TOKEN_NOT_NEED_INTERCEPT_URI = new ArrayList<>() {
        {
            add("/auth/register");
            add("/auth/login");
            add("/auth/activate/**");
            add("/auth/check/email/code");
            add("/auth/email/code");
            add("/captcha");
            add("/encrypt/rsa/public/key");
        }
    };

    /**
     * 注册sa-token的拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(SA_TOKEN_NOT_NEED_INTERCEPT_URI);
    }

    /**
     * 校验是否从网关转发
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定拦截的路由
                .addInclude("/**")
                // 排除不需要拦截的资源
                .addExclude("/favicon.ico", "/actuator/**")
                // 认证函数
                .setAuth(obj -> {
                    // 这里可以自定义全局认证逻辑
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> new ServiceException(ErrorCode.USER_ERROR_A0301))
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("antony-server")
                            // 是否可以在iframe显示视图
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 启用浏览器默认XSS防护
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff");
                });


    }


    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
