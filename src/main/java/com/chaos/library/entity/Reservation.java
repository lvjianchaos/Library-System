package com.chaos.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long id;
    private Long userId;
    private Long bookId;
    /**
     * 状态：0=排队中，1=已取消，2=已完成
     */
    private Integer status;
    private LocalDateTime createdAt;

    // 冗余字段
    private String bookTitle;
    private String userName;
}