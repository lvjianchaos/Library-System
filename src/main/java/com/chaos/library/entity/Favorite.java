package com.chaos.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime createdAt;

    // 冗余字段
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverUrl;
}