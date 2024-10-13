package com.example.template.exception.customize;

import com.example.template.common.base.ErrorCodeEnum;
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

    public ErrorCodeEnum errorCode;

    public String message;

}
