package com.chaos.library.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 返回给前端的用户信息 (脱敏，不含密码)
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private String name;
    private String phone;
    private LocalDateTime createdAt;
}