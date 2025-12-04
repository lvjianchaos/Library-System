package com.chaos.library.mapper;

import com.chaos.library.entity.Borrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BorrowMapper {

    int insert(Borrow borrow);

    int update(Borrow borrow);

    Borrow findById(Long id);

    /**
     * 查找用户当前正在借阅某本书的记录（未归还）
     */
    Borrow findByUserIdAndBookIdAndStatus(@Param("userId") Long userId,
                                          @Param("bookId") Long bookId,
                                          @Param("status") Integer status);

    /**
     * 查询某用户的借阅列表 (包含书名)
     */
    List<Borrow> findListByUserId(@Param("userId") Long userId);

    /**
     * 管理员查询所有借阅记录
     */
    List<Borrow> findAll();
}