package com.chaos.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用于创建或更新图书
 */
@Data
public class BookSaveRequest {

    @NotBlank(message = "书名不能为空")
    private String title;

    @NotBlank(message = "作者不能为空")
    private String author;

    private String description;

    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    private Integer publishYear;

    private String coverUrl;

    @NotNull(message = "总数不能为空")
    @Min(value = 1, message = "总数至少为1")
    private Integer total;

    // 标签ID列表
    private List<Long> tagIds;
}