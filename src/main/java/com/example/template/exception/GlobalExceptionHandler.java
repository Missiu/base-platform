package com.example.template.exception;

import cn.hutool.core.util.ObjectUtil;
import com.example.template.common.base.BaseResponse;
import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.exception.customize.ClientException;
import com.example.template.exception.customize.ServerException;
import com.example.template.util.ExceptionThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Security全局异常处理器
 * <p>
 * 可以预检异常不应该通过 try-catch 的方式来处理:
 * 1. NullPointerException 在使用对象前进行 null 检查
 * 2. IndexOutOfBoundsException 在访问数组、列表或字符串之前检查索引是否在合法范围内。
 * 3. IllegalArgumentException 在调用方法前验证参数的合法性。
 * 4. IllegalStateException 在执行操作前检查对象状态是否符合预期。
 * 5. ClassCastException 在类型转换时检查类型是否兼容。
 * 7. UnsupportedOperationException 在执行操作之前检查对象是否支持该操作，或根据文档了解哪些操作是不支持的。
 * 8. NegativeArraySizeException 在创建数组之前检查数组大小是否为正数。
 * 9. ArrayStoreException 确保要插入到数组中的对象与数组类型匹配。
 * 11. ConcurrentModificationException 在迭代集合或数组时，检查集合或数组是否被修改。
 * 12. IllegalMonitorStateException 在调用同步方法或块时，检查监视器是否有效。
 * 13. EmptyStackException 在调用栈的 pop() 或 peek() 方法时，检查栈是否为空。
 * 14. NoSuchElementException 在调用迭代器的 next() 方法时，检查迭代器是否有下一个元素。
 * ...
 *
 * @author hzh
 * @data 2024/10/7 11:19
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 服务端异常处理
     */
    @ExceptionHandler(ServerException.class)
    public BaseResponse<Void> serverExceptionHandler(ServerException e, HttpServletRequest request) {
        return handleException(e, request, "服务端异常", e.getErrorCode(), e.getMessage());
    }

    /**
     * 客户端异常处理
     */
    @ExceptionHandler(ClientException.class)
    public BaseResponse<Void> clientExceptionHandler(ClientException e, HttpServletRequest request) {
        return handleException(e, request, "客户端异常", e.getErrorCode(), e.getMessage());
    }

    /**
     * 运行时异常处理 (未知异常)
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<Void> runtimeExceptionHandler(RuntimeException e, HttpServletRequest request) {
        return handleException(e, request, "系统异常", ErrorCodeEnum.SYSTEM_ERROR_B0001, e.getMessage());
    }

    /**
     * 统一异常处理逻辑
     */
    private BaseResponse<Void> handleException(Exception e, HttpServletRequest request, String errorType, ErrorCodeEnum errorCode, String errorMsg) {
        String stackTraceSummary = ExceptionThrowUtils.formatExceptionStackTrace(e);
        if (ObjectUtil.isNull(errorMsg)) {
            log.error("{} 发生异常, 异常类型: {}, 请求路径: {}, 错误码-错误信息: {}-{}, 原始错误信息: {}, 异常堆栈信息: \n{}",
                    e.getClass().getName(), errorType, request.getRequestURI(), errorCode.getCode(), errorCode.getMessage(), e.getMessage(), stackTraceSummary);

        } else {
            log.error("{} 发生异常, 异常类型: {}, 请求路径: {}, 错误码: {}, 自定义错误信息: {}, 原始错误信息: {}, 异常堆栈信息: \n{}",
                    e.getClass().getName(), errorType, request.getRequestURI(), errorCode.getCode(), errorMsg, e.getMessage(), stackTraceSummary);

        }
        return BaseResponse.error(errorCode);
    }
}