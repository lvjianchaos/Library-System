package com.chaos.library.service.impl;

import com.chaos.library.common.errorcode.BookErrorCode;
import com.chaos.library.common.errorcode.BorrowErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.entity.Book;
import com.chaos.library.entity.Borrow;
import com.chaos.library.entity.Reservation;
import com.chaos.library.mapper.BookMapper;
import com.chaos.library.mapper.BorrowMapper;
import com.chaos.library.mapper.ReservationMapper;
import com.chaos.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowServiceImpl implements BorrowService {

    private final BorrowMapper borrowMapper;
    private final BookMapper bookMapper;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(Long userId, Long bookId) {
        // 1. 检查图书是否存在
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new ClientException(BookErrorCode.BOOK_NOT_FOUND);
        }

        // 2. 检查库存
        if (book.getStock() <= 0) {
            throw new ClientException(BorrowErrorCode.STOCK_NOT_ENOUGH);
        }

        // 3. 检查用户是否已借阅该书且未还（防止重复借阅同一本书）
        Borrow existBorrow = borrowMapper.findByUserIdAndBookIdAndStatus(userId, bookId, 0);
        if (existBorrow != null) {
            throw new ClientException(BorrowErrorCode.BOOK_ALREADY_BORROWED);
        }

        // 4. 扣减库存
        int rows = bookMapper.updateStock(bookId, -1);
        if (rows == 0) {
            throw new ClientException(BorrowErrorCode.STOCK_NOT_ENOUGH);
        }

        // 5. 创建借阅记录
        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setDueTime(LocalDateTime.now().plusDays(30)); // 默认借阅30天
        borrow.setStatus(0); // 借出中
        borrowMapper.insert(borrow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, Long bookId) {
        // 1. 查找借阅记录
        Borrow borrow = borrowMapper.findByUserIdAndBookIdAndStatus(userId, bookId, 0);
        if (borrow == null) {
            throw new ClientException(BorrowErrorCode.BOOK_NOT_BORROWED);
        }

        // 2. 更新借阅状态为已归还
        borrow.setStatus(1);
        borrow.setReturnTime(LocalDateTime.now());
        borrowMapper.update(borrow);

        // ================= 核心逻辑：预约流转 =================

        // 3. 检查是否有预约中的用户（按时间排序最早的一个）
        Reservation reservation = reservationMapper.findFirstPendingReservation(bookId);

        if (reservation != null) {
            log.info("触发预约流转: BookId={}, FromUserId={}, ToUserId={}", bookId, userId, reservation.getUserId());

            // 3.1 完成预约
            reservationMapper.updateStatus(reservation.getId(), 2); // 2=已完成

            // 3.2 自动为预约用户创建借阅记录
            // 注意：此时不需要更新库存，因为书从 A 手里直接流转给了 B
            Borrow newBorrow = new Borrow();
            newBorrow.setUserId(reservation.getUserId());
            newBorrow.setBookId(bookId);
            newBorrow.setBorrowTime(LocalDateTime.now());
            newBorrow.setDueTime(LocalDateTime.now().plusDays(30));
            newBorrow.setStatus(0);
            borrowMapper.insert(newBorrow);

        } else {
            // 3.3 无人预约，库存回滚 +1
            bookMapper.updateStock(bookId, 1);
        }
    }

    @Override
    public List<Borrow> getMyBorrows(Long userId) {
        return borrowMapper.findListByUserId(userId);
    }
}