package com.example.template.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Spring容器上下文类
 *
 * @author hzh
 * @data 2024/10/5 10:59
 */
@Component
@Slf4j
public class SpringContextUtils implements ApplicationContextAware {

    /**
     * 以静态变量保存ApplicationContext,可在任意代码中取出ApplicationContext.
     */
    private static ApplicationContext applicationContext;


    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz Bean类
     * @param <T>   泛型T
     * @return 返回结果
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  Bean名称
     * @param clazz Bean类
     * @param <T>   泛型T
     * @return 返回结果
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 通过class获取Bean.
     *
     * @param name Bean类名称
     * @return 返回结果
     */
    public static Object getBeans(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 获取所有的Bean名称
     *
     * @return 返回结果
     */
    public static String[] getBeanDefinitionNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    /**
     * 根据类型获取Bean名称
     *
     * @param clazz 类型
     * @param <T>   泛型T
     * @return 返回结果
     */
    public static <T> String[] getBeanNamesForType(Class<T> clazz) {
        return applicationContext.getBeanNamesForType(clazz);
    }

    /**
     * 获取applicationContext
     *
     * @return 返回结果
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}