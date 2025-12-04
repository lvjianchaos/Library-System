package com.chaos.library.config.security;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.result.Results;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 处理 401 未登录异常
 * 确保返回统一 JSON 格式
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // 保持HTTP 200，前端根据 code 判断业务逻辑

        // 返回 A000131 用户未登录
        String json = objectMapper.writeValueAsString(
                Results.failure(BaseErrorCode.USER_NOT_LOGIN.code(), BaseErrorCode.USER_NOT_LOGIN.message())
        );
        response.getWriter().write(json);
    }
}