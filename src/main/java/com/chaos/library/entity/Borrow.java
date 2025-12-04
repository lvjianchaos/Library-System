package com.chaos.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Borrow {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    /**
     * 状态：0=借出中，1=已归还，2=逾期
     */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 冗余字段，用于关联查询时封装结果 (MyBatis映射用)
    private String bookTitle;
    private String bookIsbn;
    private String userName;
}