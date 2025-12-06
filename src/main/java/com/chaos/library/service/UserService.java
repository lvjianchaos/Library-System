package com.chaos.library.service;

import com.chaos.library.dto.*;
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

    /**
     * 获取当前用户详情 (DTO)
     */
    UserDto getUserProfile(Long userId);

    /**
     * 更新用户基础信息
     */
    void updateUserInfo(Long userId, UserUpdateInfoRequest request);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, UserUpdatePasswordRequest request);
}