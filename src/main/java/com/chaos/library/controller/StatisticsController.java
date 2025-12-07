package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.dto.ActivityDto;
import com.chaos.library.dto.TopBookDto;
import com.chaos.library.dto.TopUserDto;
import com.chaos.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取借阅动态
     * URL: /api/statistics/activities?limit=10
     */
    @GetMapping("/activities")
    public Result<List<ActivityDto>> getActivities(@RequestParam(defaultValue = "10") int limit) {
        return Results.success(statisticsService.getRecentActivities(limit));
    }

    /**
     * 获取借阅达人榜
     * URL: /api/statistics/top-users?limit=5
     */
    @GetMapping("/top-users")
    public Result<List<TopUserDto>> getTopUsers(@RequestParam(defaultValue = "5") int limit) {
        return Results.success(statisticsService.getTopUsers(limit));
    }

    /**
     * 获取热门图书榜
     * URL: /api/statistics/top-books?limit=5
     */
    @GetMapping("/top-books")
    public Result<List<TopBookDto>> getTopBooks(@RequestParam(defaultValue = "5") int limit) {
        return Results.success(statisticsService.getTopBooks(limit));
    }
}