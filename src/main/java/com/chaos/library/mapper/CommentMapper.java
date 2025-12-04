package com.chaos.library.mapper;

import com.chaos.library.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    int insert(Comment comment);

    int deleteById(Long id);

    /**
     * 获取某本书的所有评论 (包含用户信息)
     */
    List<Comment> findByBookId(Long bookId);

    /**
     * 获取某用户的评论列表
     */
    List<Comment> findByUserId(Long userId);

    Comment findById(Long id);
}