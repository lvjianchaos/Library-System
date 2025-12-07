package com.chaos.library.dto;

import lombok.Data;

@Data
public class TopBookDto {
    private String bookTitle;
    private Integer borrowCount;
}