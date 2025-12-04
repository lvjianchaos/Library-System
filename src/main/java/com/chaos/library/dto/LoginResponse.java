package com.chaos.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String role;
}