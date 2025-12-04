package com.chaos.library.common.errorcode;

public enum UserErrorCode implements IErrorCode {

    USER_NOT_FOUND("A000101", "用户不存在"),
    USER_PASSWORD_ERROR("A000102", "密码错误"),
    USER_ALREADY_EXISTS("A000103", "用户名已存在"),
    USER_ROLE_INVALID("A000104", "用户角色非法");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}