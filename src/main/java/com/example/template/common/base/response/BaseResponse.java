package com.example.template.common.base.response;

import com.example.template.common.base.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础返回
 * 不建议直接使用message字段，建议使用错误码枚举
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

    @Schema(description = "消息内容")
    private String message;

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "返回数据")
    private T data;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param data    返回数据
     * @param message 消息内容
     * @param <T>     返回的数据类型
     * @return BaseResponse
     */
    private static <T> BaseResponse<T> build(String code, String message, boolean success, T data) {
        BaseResponse<T> result = new BaseResponse<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(success);
        return result;
    }

    /**
     * 成功返回，默认返回数据为null
     * 必须添加注释充分说明什么情况下会返回null值
     *
     * @param <T> 返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success() {
        return build(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), true, null);
    }

    /**
     * 成功返回,可以自定义消息内容,默认返回数据为null
     * 必须添加注释充分说明什么情况下会返回null值
     *
     * @param <T> 返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(String message) {
        return build(ErrorCode.SUCCESS.getCode(), message, true, null);
    }

    /**
     * 成功返回，可以是空数组或者空对象,也可以是其他数据
     *
     * @param data 返回的数据
     * @param <T>  返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return build(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), true, data);
    }

    /**
     * 成功返回，自定义消息内容，可以是空数组或者空对象,也可以是其他数据,不建议
     *
     * @param data    返回的数据
     * @param message 自定义消息内容
     * @param <T>     返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return build(ErrorCode.SUCCESS.getCode(), message, true, data);
    }

    /**
     * 错误返回，方法的返回值可以为null，不强制返回空集合，或者空对象
     *
     * @param errorCode 错误码枚举
     * @param data          返回的数据
     * @param <T>           返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, T data) {
        return build(errorCode.getCode(), errorCode.getMessage(), false, data);
    }

    /**
     * 必须添加注释充分说明什么情况下会返回null值
     *
     * @param errorCode 错误码枚举
     * @param <T>           返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return build(errorCode.getCode(), errorCode.getMessage(), false, null);
    }

    /**
     * 自定义消息内容
     *
     * @param errorCode 错误码枚举
     * @param message       自定义消息内容
     * @param <T>           返回的数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return build(errorCode.getCode(), message, false, null);
    }
}
