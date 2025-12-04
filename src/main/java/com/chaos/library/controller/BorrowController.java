package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.common.util.JwtUtils;
import com.chaos.library.entity.Borrow;
import com.chaos.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;
    private final JwtUtils jwtUtils;

    /**
     * 借书
     */
    @PostMapping
    public Result<Void> borrow(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long bookId = params.get("bookId");
        Long userId = getUserIdFromRequest(request);
        borrowService.borrowBook(userId, bookId);
        return Results.success();
    }

    /**
     * 还书
     */
    @PostMapping("/return")
    public Result<Void> returnBook(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long bookId = params.get("bookId");
        Long userId = getUserIdFromRequest(request);
        borrowService.returnBook(userId, bookId);
        return Results.success();
    }

    /**
     * 我的借阅列表
     */
    @GetMapping("/list")
    public Result<List<Borrow>> list(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Results.success(borrowService.getMyBorrows(userId));
    }

    // 辅助方法：从 Token 获取 UserId
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtils.extractUserId(token);
    }
}