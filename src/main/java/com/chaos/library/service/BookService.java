package com.chaos.library.service;

import com.chaos.library.dto.BookDto;
import com.chaos.library.dto.BookQuery;
import com.chaos.library.dto.BookSaveRequest;
import com.chaos.library.common.result.PageResult;
import com.chaos.library.entity.Book;

public interface BookService {

    /**
     * 创建图书 (Admin)
     */
    void createBook(BookSaveRequest request);

    /**
     * 更新图书 (Admin)
     */
    void updateBook(Long id, BookSaveRequest request);

    /**
     * 删除图书 (Admin)
     */
    void deleteBook(Long id);

    /**
     * 获取单个图书详情
     */
    BookDto getBookById(Long id);

    /**
     * 分页搜索图书
     */
    PageResult<Book> getBookList(BookQuery query);
}