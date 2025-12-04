package com.chaos.library.mapper;

import com.chaos.library.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

    int insert(Reservation reservation);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查找某本书最早的一个“排队中”预约
     */
    Reservation findFirstPendingReservation(@Param("bookId") Long bookId);

    /**
     * 检查用户是否已经预约过该书且未完成
     */
    Reservation findPendingByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 查找用户的预约列表
     */
    List<Reservation> findListByUserId(@Param("userId") Long userId);
}