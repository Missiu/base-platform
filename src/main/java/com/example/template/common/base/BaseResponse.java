package com.example.template.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础返回
 *
 * @author hzh
 * @data 2024/10/5 10:59
 */
@Data
@Schema(description = "基础返回")
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "返回数据")
    private T data;

    @Schema(description = "消息内容")
    private String message;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param data    返回数据
     * @param message 消息内容
     * @param <T>     返回的数据类型
     * @return BaseResponse
     */
    private static <T> BaseResponse<T> build(String code, T data, String message) {
        BaseResponse<T> result = new BaseResponse<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    /**
     * 成功返回，默认返回数据为null
     *
     * @param data 返回的数据
     * @param <T>  返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return build(ErrorCodeEnum.SUCCESS.getCode(), data, ErrorCodeEnum.SUCCESS.getMessage());
    }

    /**
     * 自定义消息内容，不建议
     *
     * @param data    返回的数据
     * @param message 自定义消息内容
     * @param <T>     返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return build(ErrorCodeEnum.SUCCESS.getCode(), data, message);
    }

    /**
     * 方法的返回值可以为null，不强制返回空集合，或者空对象等
     *
     * @param errorCodeEnum 错误码枚举
     * @param data          返回的数据
     * @param <T>           返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCodeEnum errorCodeEnum, T data) {
        return build(errorCodeEnum.getCode(), data, errorCodeEnum.getMessage());
    }

    /**
     * 必须添加注释充分说明什么情况下会返回null值
     *
     * @param errorCodeEnum 错误码枚举
     * @param <T>           返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCodeEnum errorCodeEnum) {
        return build(errorCodeEnum.getCode(), null, errorCodeEnum.getMessage());
    }

}
