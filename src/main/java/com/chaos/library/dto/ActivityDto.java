package com.chaos.library.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityDto {
    private String userName;
    private String bookTitle;
    /**
     * 动作类型： "借阅" 或 "归还"
     */
    private String action;
    private LocalDateTime time;
}