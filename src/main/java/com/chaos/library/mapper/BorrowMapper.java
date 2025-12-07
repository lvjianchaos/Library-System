package com.chaos.library.mapper;

import com.chaos.library.dto.ActivityDto;
import com.chaos.library.dto.TopBookDto;
import com.chaos.library.dto.TopUserDto;
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

    /**
     * 获取最近的借阅/归还动态
     */
    List<ActivityDto> findRecentActivities(@Param("limit") int limit);

    /**
     * 获取借阅次数最多的用户
     */
    List<TopUserDto> findTopUsers(@Param("limit") int limit);

    /**
     * 获取被借阅次数最多的图书
     */
    List<TopBookDto> findTopBooks(@Param("limit") int limit);
}