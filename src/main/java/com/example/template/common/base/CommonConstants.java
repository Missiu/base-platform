package com.example.template.common.base;

/**
 * 通用常量
 *
 * @author hzh
 * @data 2024/10/5 10:33
 */
public interface CommonConstants {

    /**
     * 已删除
     */
    Integer IS_DELETED = 1;

    /**
     * 未删除
     */
    Integer IS_NOT_DELETED = 0;

    /**
     * RSA加密算法
     */
    String RSA = "RSA";
    /**
     * SHA-256加密算法
     */
    String SHA256 = "SHA-256";

    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * www主域
     */
    String WWW = "www.";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 未知文件类型后缀
     */
    String UNKNOWN_FILE_TYPE_SUFFIX = "unknown";

    /**
     * 未知文件ContentType
     */
    String UNKNOWN_FILE_CONTENT_TYPE = "application/octet-stream";
}
