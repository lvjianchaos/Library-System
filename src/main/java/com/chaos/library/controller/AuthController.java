package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.common.util.JwtUtils;
import com.chaos.library.dto.*;
import com.chaos.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Results.success(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserDto> getProfile(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Results.success(userService.getUserProfile(userId));
    }

    /**
     * 修改基础信息 (姓名、电话)
     */
    @PutMapping("/me")
    public Result<Void> updateInfo(@RequestBody @Valid UserUpdateInfoRequest request, HttpServletRequest requestHttp) {
        Long userId = getUserIdFromRequest(requestHttp);
        userService.updateUserInfo(userId, request);
        return Results.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Valid UserUpdatePasswordRequest request, HttpServletRequest requestHttp) {
        Long userId = getUserIdFromRequest(requestHttp);
        userService.updatePassword(userId, request);
        return Results.success();
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtils.extractUserId(token);
    }
}