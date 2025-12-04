package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.common.util.JwtUtils;
import com.chaos.library.entity.Reservation;
import com.chaos.library.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtUtils jwtUtils;

    /**
     * 预约图书
     */
    @PostMapping
    public Result<Void> reserve(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long bookId = params.get("bookId");
        Long userId = getUserIdFromRequest(request);
        reservationService.reserveBook(userId, bookId);
        return Results.success();
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result<Void> cancel(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long reservationId = params.get("reservationId");
        Long userId = getUserIdFromRequest(request);
        reservationService.cancelReservation(userId, reservationId);
        return Results.success();
    }

    /**
     * 我的预约列表
     */
    @GetMapping("/list")
    public Result<List<Reservation>> list(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Results.success(reservationService.getMyReservations(userId));
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtils.extractUserId(token);
    }
}