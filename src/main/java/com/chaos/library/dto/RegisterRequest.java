package com.chaos.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度需在4-50字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20字符之间")
    private String password;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    // 可选，不传默认为 STUDENT
    @Pattern(regexp = "^(admin|student|ADMIN|STUDENT)$", message = "角色必须是 admin 或 student")
    private String role;
}