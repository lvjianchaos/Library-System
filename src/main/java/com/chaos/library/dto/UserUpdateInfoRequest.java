package com.chaos.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateInfoRequest {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;
}