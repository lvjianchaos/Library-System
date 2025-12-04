package com.chaos.library.dto;

import lombok.Data;

@Data
public class BookQuery {
    // 分页参数
    private Integer limit;  // 每页数量
    private Integer offset; // 偏移量

    // 筛选参数
    private String title;
    private String author;
    private String isbn;
    private String tagName; // 根据标签名筛选，如 "科幻"
}