package com.chaos.library.service;

import com.chaos.library.entity.Reservation;
import java.util.List;

public interface ReservationService {

    /**
     * 预约图书
     */
    void reserveBook(Long userId, Long bookId);

    /**
     * 取消预约
     */
    void cancelReservation(Long userId, Long reservationId);

    /**
     * 获取我的预约列表
     */
    List<Reservation> getMyReservations(Long userId);
}