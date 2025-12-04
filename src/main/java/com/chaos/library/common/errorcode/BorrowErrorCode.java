package com.chaos.library.common.errorcode;

public enum BorrowErrorCode implements IErrorCode {

    STOCK_NOT_ENOUGH("A000401", "库存不足，请进行预约"),
    BOOK_ALREADY_BORROWED("A000402", "您已借阅过该书且未归还"),
    BOOK_NOT_BORROWED("A000403", "未找到该书的在借记录"),
    RESERVATION_EXISTS("A000404", "您已预约过该书"),
    RESERVATION_NOT_NEEDED("A000405", "当前有库存，无需预约"),
    RESERVATION_NOT_FOUND("A000406", "预约记录不存在");

    private final String code;
    private final String message;

    BorrowErrorCode(String code, String message) {
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