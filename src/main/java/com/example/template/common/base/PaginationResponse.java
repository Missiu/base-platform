package com.example.template.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页返回
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
@Data
@Schema(description = "分页返回")
public class PaginationResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "列表数据")
    private List<T> rows;

    @Schema(description = "总记录数")
    private Integer total;

    @Schema(description = "消息内容")
    private String message;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param rows    列表数据
     * @param total   总记录数
     * @param message 消息内容
     * @param <T>     返回的数据类型
     * @return PaginationResponse
     */
    private static <T> PaginationResponse<T> build(String code, List<T> rows, Integer total, String message) {
        PaginationResponse<T> result = new PaginationResponse<>();
        result.setCode(code);
        result.setTotal(total);
        result.setRows(rows);
        result.setMessage(message);
        return result;
    }

    /**
     * 返回成功,构建分页
     *
     * @param <T> 返回的数据类型
     * @return PageResponse<T>
     */
    public static <T> PaginationResponse<T> buildPaginationSuccess(List<T> list) {
        return build(ErrorCodeEnum.SUCCESS.getCode(), list, list.size(), ErrorCodeEnum.SUCCESS.getMessage());
    }

    /**
     * 返回成功,构建分页
     *
     * @param list    列表数据
     * @param message 消息内容
     * @param <T>     返回的数据类型
     * @return PaginationResponse<T>
     */
    public static <T> PaginationResponse<T> buildPaginationSuccess(List<T> list, String message) {
        return build(ErrorCodeEnum.SUCCESS.getCode(), list, list.size(), message);
    }

    /**
     * 返回失败,构建分页
     *
     * @param list          列表数据
     * @param <T>           返回的数据类型
     * @param errorCodeEnum 错误码枚举
     * @return PaginationResponse<T>
     */
    public static <T> PaginationResponse<T> buildPaginationError(ErrorCodeEnum errorCodeEnum, List<T> list) {
        return build(errorCodeEnum.getCode(), list, 0, errorCodeEnum.getMessage());
    }

    /**
     * 返回失败,构建分页
     *
     * @param errorCodeEnum 错误码枚举
     * @return PaginationResponse<T>
     */
    public static <T> PaginationResponse<T> buildPaginationError(ErrorCodeEnum errorCodeEnum) {
        return build(errorCodeEnum.getCode(), null, 0, errorCodeEnum.getMessage());
    }
}
