package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.dto.LoginRequest;
import com.chaos.library.dto.LoginResponse;
import com.chaos.library.dto.RegisterRequest;
import com.chaos.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

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
}