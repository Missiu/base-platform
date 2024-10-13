package com.example.template.exception.customize;

import com.example.template.common.base.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统异常处理
 * 默认系统端错误码 SYSTEM_ERROR_B0001。适用于服务器内部未知错误。
 *
 * @author hzh
 * @data 2024/10/7 11:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ServiceException extends BaseException {


    /**
     * 构造函数，默认系统端错误码 SYSTEM_ERROR_B0001 (系统执行出错)。
     */
    public ServiceException() {
        this.errorCode = ErrorCodeEnum.SYSTEM_ERROR_B0001;
    }

    /**
     * 构造函数，自定义错误码枚举。
     *
     * @param errorCode 自定义错误码枚举
     */
    public ServiceException(ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 构造函数，自定义错误码和错误信息。
     * 根据错误码步长区分不同系统错误
     * 比如 ： 一级宏观错误码、二级宏观错误码
     *
     * @param errorCode 自定义错误码
     * @param message   自定义错误信息
     */
    public ServiceException(ErrorCodeEnum errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
