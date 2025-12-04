package com.chaos.library.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Book {
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
}