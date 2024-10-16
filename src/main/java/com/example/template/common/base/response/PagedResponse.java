package com.example.template.common.base.response;

import cn.dev33.satoken.stp.StpUtil;
import com.example.template.common.base.ErrorCode;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页返回
 * 不建议直接使用message字段，建议使用错误码枚举
 *
 * @author hzh
 * @date 2024/10/5 11:40
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分页返回")
public class PagedResponse<T> extends GeneralResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private int pageNum;

    @Schema(description = "每页大小")
    private int pageSize;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "列表数据")
    private List<T> rows;

    /**
     * 构造分页返回
     *
     * @param traceId    溯源ID
     * @param code       状态码
     * @param message    消息
     * @param success    是否成功
     * @param pageSize   每页大小
     * @param total      总记录数
     * @param rows       列表数据
     * @param <T>        返回的数据类型
     * @return PagedResponse<T>
     */
    private static <T> PagedResponse<T> build(String traceId, String code, String message, boolean success, int pageNum, int pageSize, long total, List<T> rows) {
        PagedResponse<T> result = new PagedResponse<>();
        result.setTraceId(traceId);
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(success);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setRows(rows);
        return result;
    }


    /**
     * 成功返回分页数据
     *
     * @param rows 列表数据
     * @param <T>  返回的数据类型
     * @return PagedResponse<T>
     */
    public static <T> PagedResponse<T> success(List<T> rows) {
        PageInfo<T> pageInfo = new PageInfo<>(rows);
        return build(StpUtil.getTokenValue(),
                ErrorCode.SUCCESS.getCode(),
                ErrorCode.SUCCESS.getMessage(),
                true,
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal(),
                rows);
    }

    /**
     * 成功返回分页数据，支持自定义消息
     *
     * @param rows    列表数据
     * @param message 自定义消息
     * @param <T>     返回的数据类型
     * @return PagedResponse<T>
     */
    public static <T> PagedResponse<T> success(List<T> rows, String message) {
        PageInfo<T> pageInfo = new PageInfo<>(rows);
        return build(StpUtil.getTokenValue(),
                ErrorCode.SUCCESS.getCode(),
                message,
                true,
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal(),
                rows);
    }

}
