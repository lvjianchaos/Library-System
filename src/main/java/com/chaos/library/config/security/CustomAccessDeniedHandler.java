package com.chaos.library.config.security;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.result.Results;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 处理 403 权限不足异常
 * 确保返回统一 JSON 格式
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // 返回 A000132 权限不足
        String json = objectMapper.writeValueAsString(
                Results.failure(BaseErrorCode.USER_NO_PERMISSION.code(), BaseErrorCode.USER_NO_PERMISSION.message())
        );
        response.getWriter().write(json);
    }
}