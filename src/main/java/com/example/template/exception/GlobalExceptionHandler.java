package com.example.template.exception;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.common.base.response.BaseResponse;
import com.example.template.common.base.response.GeneralResponse;
import com.example.template.exception.customize.ClientException;
import com.example.template.exception.customize.RemoteServiceException;
import com.example.template.exception.customize.ServiceException;
import com.example.template.util.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

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
    @ExceptionHandler(ServiceException.class)
    public BaseResponse<Void> serverExceptionHandler(ServiceException e, HttpServletRequest request) {
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
     * 第三方异常处理
     */
    @ExceptionHandler(RemoteServiceException.class)
    public BaseResponse<Void> remoteServiceExceptionHandler(RemoteServiceException e, HttpServletRequest request) {
        return handleException(e, request, "第三方服务异常", e.getErrorCode(), e.getMessage());
    }

    /**
     * 其他异常处理 (未知异常)
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<Void> runtimeExceptionHandler(Exception e, HttpServletRequest request) {
        return handleException(e, request, "其他异常", ErrorCodeEnum.SYSTEM_ERROR_B0001, e.getMessage());
    }

    /**
     * Validation 处理 form data方式调用接口校验失败抛出的异常
     */
    @ExceptionHandler(BindException.class)
    public BaseResponse<Void> bindExceptionHandler(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorMessage = String.join(", ", collect);
        return handleException(e, request, "客户端异常", ErrorCodeEnum.USER_ERROR_0001, errorMessage);
    }

    /**
     * Validation 处理 json 请求体调用接口校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> collect = allErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorMessage = String.join(", ", collect);
        return handleException(e, request, "客户端异常", ErrorCodeEnum.USER_ERROR_0001, errorMessage);
    }

    /**
     * Validation 处理单个参数校验失败抛出的异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<Void> constraintViolationExceptionHandler(ConstraintViolationException e, HttpServletRequest request) {
        return handleException(e, request, "客户端异常", ErrorCodeEnum.USER_ERROR_0001, Objects.requireNonNull(e.getConstraintViolations().stream().findFirst().orElse(null)).getMessage());
    }

    /**
     * 统一异常处理逻辑
     */
    private BaseResponse<Void> handleException(Exception e, HttpServletRequest request, String errorType, ErrorCodeEnum errorCode, String errorMessage) {
        // 获取请求的URI和Referer
        String requestURI = request.getRequestURI();
        String referer = request.getHeader("Referer");

        // 构建基础的请求信息，带上请求URL和Referer信息
        String requestInfo = String.format("请求 [URL: %s] 的Referer [%s]", requestURI, referer == null ? "null" : referer);

        // 根据errorMsg是否为空，动态生成最终的错误信息
        String finalErrorMsg = ObjectUtil.isNotEmpty(errorMessage) ? errorMessage : errorCode.getMessage();

        // 拼接成完整的错误信息
        finalErrorMsg = String.format("%s 发生 [%s] 内容为 [%s]", requestInfo, errorType, finalErrorMsg);

        // 获取异常的堆栈摘要信息
        String stackTraceSummary = ThrowUtils.formatExceptionStackTrace(e);

        // 日志处理
        log.error("{}-{} 发生异常, \n 错误码: {}, 错误信息: {}, 原始错误信息: {} \n 异常发生地址: {}, 请求路径: {} \n 异常堆栈信息: \n{}",
                errorType, e.getClass().getName(), errorCode.getCode(), finalErrorMsg, e.getLocalizedMessage(),
                requestURI, errorType, stackTraceSummary);

        // 判断是否登录用户，决定返回的响应内容
        if (StpUtil.isLogin()) {
            // 获取 traceId 并返回带 traceId 的响应
            String traceId = StpUtil.getTokenValue();
            return GeneralResponse.error(errorCode, finalErrorMsg, traceId);
        } else {
            // 返回没有 traceId 的 BaseResponse
            return BaseResponse.error(errorCode, finalErrorMsg);
        }
    }
}