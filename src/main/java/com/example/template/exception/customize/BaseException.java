package com.example.template.exception.customize;

import com.example.template.common.base.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础异常类
 *
 * @author hzh
 * @data 2024/10/7 11:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    /**
     * 错误码
     */
    public ErrorCode errorCode;

    /**
     * 错误信息
     */
    public String message;

}
