package com.chaos.library.mapper;

import com.chaos.library.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    int insert(Favorite favorite);

    int deleteByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 检查是否已收藏
     */
    Favorite findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 获取我的收藏列表 (包含图书信息)
     */
    List<Favorite> findListByUserId(Long userId);
}