package com.chaos.library.common.errorcode;

public enum BookErrorCode implements IErrorCode {

    BOOK_NOT_FOUND("A000301", "图书不存在"),
    ISBN_EXIST("A000302", "ISBN编号已存在"),
    TAG_NOT_FOUND("A000303", "标签不存在");

    private final String code;
    private final String message;

    BookErrorCode(String code, String message) {
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