package com.chaos.library.service.impl;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.errorcode.UserErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.common.util.JwtUtils;
import com.chaos.library.dto.*;
import com.chaos.library.entity.User;
import com.chaos.library.mapper.UserMapper;
import com.chaos.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 1. 校验用户名是否已存在
        User existUser = userMapper.findByUsername(request.getUsername());
        if (existUser != null) {
            throw new ClientException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        // 2. 构建新用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPhone(request.getPhone());

        // 3. 密码加密
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. 设置角色，默认为 STUDENT
        String role = StringUtils.hasText(request.getRole()) ? request.getRole().toLowerCase() : "student";
        user.setRole(role);

        // 5. 插入数据库
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            // 为了安全，模糊提示“用户名或密码错误”，但在日志中可以记录详细原因
            throw new ClientException(BaseErrorCode.USER_LOGIN_ERROR);
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ClientException(BaseErrorCode.USER_LOGIN_ERROR);
        }

        // 3. 生成 Token
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole(), user.getId());

        // 4. 返回结果
        return LoginResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new ClientException(UserErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserDto getUserProfile(Long userId) {
        User user = getUserById(userId);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    @Override
    public void updateUserInfo(Long userId, UserUpdateInfoRequest request) {
        User user = getUserById(userId);
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        userMapper.update(user);
    }

    @Override
    public void updatePassword(Long userId, UserUpdatePasswordRequest request) {
        User user = getUserById(userId);

        // 1. 校验旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ClientException(UserErrorCode.USER_PASSWORD_ERROR); // 使用密码错误的错误码
        }

        // 2. 更新新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.update(user);
    }
}