package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.common.util.JwtUtils;
import com.chaos.library.entity.Comment;
import com.chaos.library.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtUtils jwtUtils;

    /**
     * 发表评论
     */
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long bookId = Long.valueOf(params.get("bookId").toString());
        String content = (String) params.get("content");
        Long userId = getUserIdFromRequest(request);

        commentService.addComment(userId, bookId, content);
        return Results.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        commentService.deleteComment(userId, id);
        return Results.success();
    }

    /**
     * 获取某本书的评论
     */
    @GetMapping("/book/{bookId}")
    public Result<List<Comment>> listByBook(@PathVariable Long bookId) {
        return Results.success(commentService.getBookComments(bookId));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtils.extractUserId(token);
    }
}