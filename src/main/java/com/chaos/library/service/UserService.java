package com.chaos.library.service;

import com.chaos.library.dto.LoginRequest;
import com.chaos.library.dto.LoginResponse;
import com.chaos.library.dto.RegisterRequest;
import com.chaos.library.entity.User;

public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);
}