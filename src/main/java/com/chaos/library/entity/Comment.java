package com.chaos.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long bookId;
    private String content;
    private LocalDateTime createdAt;

    // 冗余字段，用于前端展示评论人信息
    private String userName;
    // 冗余字段，用于展示书名（如果需要查看"我的评论"）
    private String bookTitle;
}