package com.chaos.library.dto;

import com.chaos.library.entity.Tag;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 返回给前端的图书信息
 * 对应获取单个图书时的结构，包含 tags
 */
@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String isbn;
    private Integer publishYear;
    private String coverUrl;
    private Integer total;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Tag> tags;
    // 不再需要？
    // private List<CommentDto> comments;
}