package com.chaos.library.service;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.entity.Comment;
import com.chaos.library.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    /**
     * 发表评论
     */
    public void addComment(Long userId, Long bookId, String content) {
        if (!StringUtils.hasText(content)) {
            throw new ClientException("评论内容不能为空");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBookId(bookId);
        comment.setContent(content);
        commentMapper.insert(comment);
    }

    /**
     * 删除评论 (仅限本人或管理员，这里简化为本人)
     */
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new ClientException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new ClientException(BaseErrorCode.USER_NO_PERMISSION);
        }
        commentMapper.deleteById(commentId);
    }

    /**
     * 获取某本书的评论
     */
    public List<Comment> getBookComments(Long bookId) {
        return commentMapper.findByBookId(bookId);
    }
}