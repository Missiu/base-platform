package com.example.template.exception.customize;

import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.exception.BaseException;

/**
 * 客户端异常类
 * 构造函数，默认系统端错误码 USER_ERROR_0001 (用户端错误)。
 *
 * @author hzh
 * @data 2024/10/7 12:19
 */
public class ClientException extends BaseException {
    /**
     * 构造函数，默认系统端错误码 USER_ERROR_0001 (用户端错误)。
     */
    public ClientException() {
        this.errorCode = ErrorCodeEnum.USER_ERROR_0001;
    }

    /**
     * 构造函数，自定义错误码枚举。
     *
     * @param errorCode 自定义错误码枚举
     */
    public ClientException(ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 构造函数，自定义错误码和错误信息。
     * 根据错误码步长区分不同系统错误
     * 比如 ： 一级宏观错误码、二级宏观错误码
     *
     * @param errorCode 自定义错误码枚举
     * @param message   自定义错误信息
     */
    public ClientException(ErrorCodeEnum errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
