package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.common.util.JwtUtils;
import com.chaos.library.entity.Favorite;
import com.chaos.library.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtUtils jwtUtils;

    /**
     * 添加收藏
     */
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long bookId = params.get("bookId");
        Long userId = getUserIdFromRequest(request);
        favoriteService.addFavorite(userId, bookId);
        return Results.success();
    }

    /**
     * 取消收藏
     */
    @PostMapping("/remove")
    public Result<Void> remove(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long bookId = params.get("bookId");
        Long userId = getUserIdFromRequest(request);
        favoriteService.removeFavorite(userId, bookId);
        return Results.success();
    }

    /**
     * 我的收藏列表
     */
    @GetMapping("/list")
    public Result<List<Favorite>> list(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Results.success(favoriteService.getMyFavorites(userId));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtils.extractUserId(token);
    }
}