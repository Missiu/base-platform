package com.example.template.common.base.response;

import cn.dev33.satoken.stp.StpUtil;
import com.example.template.common.base.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用返回
 * 不建议直接使用message字段，建议使用错误码枚举
 * 含有traceId字段，可用于溯源，一般针对需要登录后才能访问的接口
 *
 * @author hzh
 * @data 2024/10/5 10:59
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用返回")
public class GeneralResponse<T> extends BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "可溯源id")
    private String traceId;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param data    返回数据
     * @param message 消息内容
     * @param <T>     返回的数据类型
     * @return GeneralResponse
     */
    private static <T> GeneralResponse<T> build(String traceId, String code, String message, boolean success, T data) {
        GeneralResponse<T> result = new GeneralResponse<>();
        result.setTraceId(traceId);
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(success);
        return result;
    }

    /**
     * 成功返回，默认返回数据为null。
     * 返回null的情况：当不需要携带任何数据，仅返回成功状态时。
     *
     * @param <T> 返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> success() {
        return build(StpUtil.getTokenValue(), ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), true, null);
    }

    /**
     * 成功返回，可以自定义消息内容，默认返回数据为null。
     * 返回null的情况：当不需要携带任何数据，仅返回成功状态时。
     *
     * @param message 成功消息
     * @param <T>     返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> success(String message) {
        return build(StpUtil.getTokenValue(), ErrorCode.SUCCESS.getCode(), message, true, null);
    }

    /**
     * 成功返回，可指定溯源id和消息内容，默认返回数据为null。
     *
     * @param traceId 溯源id
     * @param message 消息内容
     * @param <T>     返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> success(String traceId, String message) {
        return build(traceId, ErrorCode.SUCCESS.getCode(), message, true, null);
    }

    /**
     * 成功返回，可以返回空数组、空对象，或者其他非null数据。
     *
     * @param data 返回的数据
     * @param <T>  返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> success(T data) {
        return build(StpUtil.getTokenValue(), ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), true, data);
    }

    /**
     * 成功返回，自定义消息内容，可以返回空数组、空对象，或者其他非null数据。
     *
     * @param data    返回的数据
     * @param message 自定义消息内容
     * @param <T>     返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> success(T data, String message) {
        return build(StpUtil.getTokenValue(), ErrorCode.SUCCESS.getCode(), message, true, data);
    }

    /**
     * 错误返回，默认返回数据为null。
     * 返回null的情况：当发生错误时，不需要返回数据，仅返回错误信息。
     *
     * @param errorCode 错误码枚举
     * @param <T>           返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> error(ErrorCode errorCode) {
        return build(StpUtil.getTokenValue(), errorCode.getCode(), errorCode.getMessage(), false, null);
    }

    /**
     * 错误返回，可以返回具体数据。
     *
     * @param errorCode 错误码枚举
     * @param data          返回的数据
     * @param <T>           返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> error(ErrorCode errorCode, T data) {
        return build(StpUtil.getTokenValue(), errorCode.getCode(), errorCode.getMessage(), false, data);
    }

    /**
     * 错误返回，自定义消息内容，默认返回数据为null。
     * 返回null的情况：当仅需要返回自定义的错误消息内容时。
     *
     * @param errorCode 错误码枚举
     * @param message       自定义消息内容
     * @param <T>           返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> error(ErrorCode errorCode, String message) {
        return build(StpUtil.getTokenValue(), errorCode.getCode(), message, false, null);
    }

    /**
     * 错误返回，自定义消息内容和溯源id，默认返回数据为null。
     *
     * @param errorCode 错误码枚举
     * @param message       自定义消息内容
     * @param traceId       溯源id
     * @param <T>           返回的数据类型
     * @return GeneralResponse
     */
    public static <T> GeneralResponse<T> error(ErrorCode errorCode, String message, String traceId) {
        return build(traceId, errorCode.getCode(), message, false, null);
    }
}
