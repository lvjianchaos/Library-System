package com.chaos.library.common.exception;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 捕获定义的 AbstractException (ClientException / ServiceException)
     */
    @ExceptionHandler(AbstractException.class)
    @ResponseStatus(HttpStatus.OK) // HTTP 状态码保持 200，错误在 code 字段体现
    @ResponseBody
    public Result<Void> handleAbstractException(AbstractException ex) {
        return Results.failure(ex);
    }

    /**
     * 2. 捕获 @Valid 校验失败的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 仅返回第一条校验错误信息
        String errorMessage = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse(BaseErrorCode.CLIENT_ERROR.message());

        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), errorMessage);
    }

    /**
     * 3. 捕获登录失败 (例如密码错误)
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Void> handleAuthenticationException(AuthenticationException ex) {
        return Results.failure(BaseErrorCode.USER_LOGIN_ERROR.code(), BaseErrorCode.USER_LOGIN_ERROR.message());
    }

    /**
     * 4. 捕获权限不足 (例如 STUDENT 访问 ADMIN 接口)
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Void> handleAccessDeniedException(AccessDeniedException ex) {
        return Results.failure(BaseErrorCode.USER_NO_PERMISSION.code(), BaseErrorCode.USER_NO_PERMISSION.message());
    }

    /**
     * 5. 捕获所有其他 RuntimeException (服务器内部错误)
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Void> handleRuntimeException(RuntimeException ex) {
        // 在此处记录日志
        log.error("Internal Server Error: ", ex);
        return Results.failure(BaseErrorCode.SERVICE_ERROR.code(), BaseErrorCode.SERVICE_ERROR.message());
    }
}