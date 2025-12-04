package com.chaos.library.service;

import com.chaos.library.entity.Borrow;
import java.util.List;

public interface BorrowService {

    /**
     * 借书
     */
    void borrowBook(Long userId, Long bookId);

    /**
     * 还书
     */
    void returnBook(Long userId, Long bookId);

    /**
     * 获取我的借阅列表
     */
    List<Borrow> getMyBorrows(Long userId);
}