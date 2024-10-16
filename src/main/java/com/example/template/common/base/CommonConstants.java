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
     * 默认分页大小
     */
    Integer PAGE_SIZE = 10;

    /**
     * 默认当前页码
     */
    Integer PAGE_NUM = 1;

    /**
     * 最大分页大小，-1 不受限制
     */
    Long MAX_PAGE_SIZE = 500L;

    /**
     * 后端排序方向：降序
     */
    String ORDER_BY_DESC = "DESC";

    /**
     * 后端排序方向：升序
     */
    String ORDER_BY_ASC = "ASC";

    /**
     * RSA加密算法
     */
    String RSA = "RSA";
    /**
     * SHA-256加密算法
     */
    String SHA256 = "SHA-256";

    /**
     * MD5加密算法
     */
    String MD5 = "MD5";

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

    /**
     * 未知文件名称
     */
    String UNKNOWN_FILE_NAME = "none.";

    /**
     * 验证码邮箱默认长度
     */
    Integer DEFAULT_CODE_LENGTH = 6;

    /**
     * 默认短信验证码长度
     */
    Integer DEFAULT_SMS_CODE_LENGTH = 4;

    /**
     * 日期格式
     */
    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
