package com.chaos.library.service;

import com.chaos.library.dto.ActivityDto;
import com.chaos.library.dto.TopBookDto;
import com.chaos.library.dto.TopUserDto;
import com.chaos.library.mapper.BorrowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BorrowMapper borrowMapper;

    public List<ActivityDto> getRecentActivities(int limit) {
        if (limit <= 0) limit = 10; // 默认值
        if (limit > 100) limit = 100; // 限制最大值
        return borrowMapper.findRecentActivities(limit);
    }

    public List<TopUserDto> getTopUsers(int limit) {
        if (limit <= 0) limit = 5;
        return borrowMapper.findTopUsers(limit);
    }

    public List<TopBookDto> getTopBooks(int limit) {
        if (limit <= 0) limit = 5;
        return borrowMapper.findTopBooks(limit);
    }
}