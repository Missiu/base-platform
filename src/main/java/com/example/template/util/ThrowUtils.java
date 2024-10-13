package com.example.template.util;

import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.exception.customize.ClientException;
import com.example.template.exception.customize.RemoteServiceException;
import com.example.template.exception.customize.ServiceException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常抛出工具类
 *
 * @author hzh
 * @data 2024/10/7 12:57
 */
public class ThrowUtils {
    /**
     * 条件成立则抛客户端异常
     *
     * @param condition     条件
     * @param errorCodeEnum 错误码枚举
     */
    public static void clientExceptionThrowIf(boolean condition, ErrorCodeEnum errorCodeEnum) {
        if (condition) {
            throw new ClientException(errorCodeEnum);
        }
    }

    /**
     * 条件成立则抛客户端异常
     *
     * @param condition     条件
     * @param errorCodeEnum 错误码枚举
     * @param message       自定义异常信息
     */
    public static void clientExceptionThrowIf(boolean condition, ErrorCodeEnum errorCodeEnum, String message) {
        if (condition) {
            throw new ClientException(errorCodeEnum, message);
        }
    }

    /**
     * 条件成立则抛服务端异常
     *
     * @param condition     条件
     * @param errorCodeEnum 错误码枚举
     */
    public static void serverExceptionThrowIf(boolean condition, ErrorCodeEnum errorCodeEnum) {
        if (condition) {
            throw new ServiceException(errorCodeEnum);
        }
    }

    /**
     * 条件成立则抛服务端异常
     *
     * @param condition     条件
     * @param errorCodeEnum 错误码枚举
     * @param message       自定义异常信息
     */
    public static void serverExceptionThrowIf(boolean condition, ErrorCodeEnum errorCodeEnum, String message) {
        if (condition) {
            throw new ServiceException(errorCodeEnum, message);
        }
    }

    /**
     * 条件成立则远程调用服务端异常
     *
     * @param condition     条件
     * @param errorCodeEnum 错误码枚举
     */
    public static void remoteServerExceptionThrowIf(boolean condition, ErrorCodeEnum errorCodeEnum) {
        if (condition) {
            throw new RemoteServiceException(errorCodeEnum);
        }
    }

    /**
     * 获取异常堆栈的
     *
     * @param e 异常
     * @return 格式化后的异常堆栈
     */
    public static String formatExceptionStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String[] stackLines = sw.toString().split("\n");

        StringBuilder summary = new StringBuilder();

        // 自动获取当前项目的包名
        String packageName = getRootPackage();

        // 打印前三行的堆栈信息
        for (int i = 0; i < Math.min(3, stackLines.length); i++) {
            summary.append(stackLines[i]).append("\n");
        }

        // 提取和当前项目包相关的堆栈信息
        boolean hasCustomPackageInfo = false;
        for (String line : stackLines) {
            if (line.contains(packageName)) {
                summary.append(line).append("\n");
                hasCustomPackageInfo = true;
            }
        }

        // 提取 "Caused by" 信息
        for (String line : stackLines) {
            if (line.contains("Caused by")) {
                summary.append(line).append("\n");
            }
        }

        // 如果没有找到任何自定义包名相关信息
        if (!hasCustomPackageInfo) {
            summary.append("\n").append("No custom package stack trace found.\n");
        }

        return summary.toString();
    }

    /**
     * 获取根包名
     * 该方法通过分析当前线程的调用栈，寻找第一个应用类的包名
     * 它假设调用该方法的代码位于应用程序的类中
     *
     * @return 根包名如果找不到，则返回空字符串
     */
    private static String getRootPackage() {
        // 获取当前线程的类加载器，并获取第一个应用类的包名
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            // 检查类名是否以常见的包前缀开始，以确定它是一个应用类
            if (className.startsWith("com.") || className.startsWith("org.")) {
                // 提取并返回包名
                return className.substring(0, className.indexOf(".", className.indexOf(".") + 1));
            }
        }
        // 如果没有找到应用类，则返回空字符串
        return "";
    }
}
