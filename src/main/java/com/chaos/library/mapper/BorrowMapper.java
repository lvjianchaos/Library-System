package com.chaos.library.mapper;

import com.chaos.library.dto.ActivityDto;
import com.chaos.library.dto.TopBookDto;
import com.chaos.library.dto.TopUserDto;
import com.chaos.library.entity.Borrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowMapper {

    @Autowired
    private DataSource dataSource;

    public int insert(Borrow borrow) {
        String sql = "INSERT INTO borrow (user_id, book_id, borrow_time, due_time, return_time, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, borrow.getUserId());
            ps.setLong(2, borrow.getBookId());
            ps.setObject(3, borrow.getBorrowTime());
            ps.setObject(4, borrow.getDueTime());
            ps.setObject(5, borrow.getReturnTime());
            ps.setInt(6, borrow.getStatus());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if(rs.next()) borrow.setId(rs.getLong(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int update(Borrow borrow) {
        StringBuilder sql = new StringBuilder("UPDATE borrow SET updated_at = NOW()");
        List<Object> params = new ArrayList<>();
        if (borrow.getReturnTime() != null) { sql.append(", return_time = ?"); params.add(borrow.getReturnTime()); }
        if (borrow.getStatus() != null) { sql.append(", status = ?"); params.add(borrow.getStatus()); }
        sql.append(" WHERE id = ?");
        params.add(borrow.getId());
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for(int i=0; i<params.size(); i++) ps.setObject(i+1, params.get(i));
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Borrow findById(Long id) {
        return querySingle("SELECT * FROM borrow WHERE id = ?", id);
    }

    public Borrow findByUserIdAndBookIdAndStatus(Long userId, Long bookId, Integer status) {
        return querySingle("SELECT * FROM borrow WHERE user_id = ? AND book_id = ? AND status = ? LIMIT 1", userId, bookId, status);
    }

    public List<Borrow> findListByUserId(Long userId) {
        String sql = "SELECT b.*, book.title as bookTitle, book.isbn as bookIsbn FROM borrow b LEFT JOIN book ON b.book_id = book.id WHERE b.user_id = ? ORDER BY b.created_at DESC";
        return queryList(sql, userId);
    }

    public List<Borrow> findAll() {
        String sql = "SELECT b.*, book.title as bookTitle, u.name as userName FROM borrow b LEFT JOIN book ON b.book_id = book.id LEFT JOIN `user` u ON b.user_id = u.id ORDER BY b.created_at DESC";
        return queryList(sql);
    }

    // 统计功能
    public List<ActivityDto> findRecentActivities(int limit) {
        String sql = "SELECT * FROM (" +
                "(SELECT u.name as userName, b.title as bookTitle, '借阅' as action, br.borrow_time as time FROM borrow br JOIN `user` u ON br.user_id = u.id JOIN book b ON br.book_id = b.id ORDER BY br.borrow_time DESC LIMIT ?) " +
                "UNION " +
                "(SELECT u.name as userName, b.title as bookTitle, '归还' as action, br.return_time as time FROM borrow br JOIN `user` u ON br.user_id = u.id JOIN book b ON br.book_id = b.id WHERE br.status = 1 ORDER BY br.return_time DESC LIMIT ?)" +
                ") as temp ORDER BY time DESC LIMIT ?";
        List<ActivityDto> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit); ps.setInt(2, limit); ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    ActivityDto dto = new ActivityDto();
                    dto.setUserName(rs.getString("userName"));
                    dto.setBookTitle(rs.getString("bookTitle"));
                    dto.setAction(rs.getString("action"));
                    Timestamp t = rs.getTimestamp("time");
                    if(t!=null) dto.setTime(t.toLocalDateTime());
                    list.add(dto);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<TopUserDto> findTopUsers(int limit) {
        String sql = "SELECT u.name as userName, COUNT(br.id) as borrowCount FROM borrow br JOIN `user` u ON br.user_id = u.id GROUP BY br.user_id ORDER BY borrowCount DESC LIMIT ?";
        List<TopUserDto> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    TopUserDto dto = new TopUserDto();
                    dto.setUserName(rs.getString("userName"));
                    dto.setBorrowCount(rs.getInt("borrowCount"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<TopBookDto> findTopBooks(int limit) {
        String sql = "SELECT b.title as bookTitle, COUNT(br.id) as borrowCount FROM borrow br JOIN book b ON br.book_id = b.id GROUP BY br.book_id ORDER BY borrowCount DESC LIMIT ?";
        List<TopBookDto> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    TopBookDto dto = new TopBookDto();
                    dto.setBookTitle(rs.getString("bookTitle"));
                    dto.setBorrowCount(rs.getInt("borrowCount"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    private Borrow querySingle(String sql, Object... params) {
        List<Borrow> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Borrow> queryList(String sql, Object... params) {
        List<Borrow> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Borrow b = new Borrow();
                    b.setId(rs.getLong("id"));
                    b.setUserId(rs.getLong("user_id"));
                    b.setBookId(rs.getLong("book_id"));
                    Timestamp t1 = rs.getTimestamp("borrow_time"); if(t1!=null) b.setBorrowTime(t1.toLocalDateTime());
                    Timestamp t2 = rs.getTimestamp("due_time"); if(t2!=null) b.setDueTime(t2.toLocalDateTime());
                    Timestamp t3 = rs.getTimestamp("return_time"); if(t3!=null) b.setReturnTime(t3.toLocalDateTime());
                    b.setStatus(rs.getInt("status"));
                    // 尝试映射关联字段
                    try { b.setBookTitle(rs.getString("bookTitle")); } catch (SQLException ignored) {}
                    try { b.setBookIsbn(rs.getString("bookIsbn")); } catch (SQLException ignored) {}
                    try { b.setUserName(rs.getString("userName")); } catch (SQLException ignored) {}
                    list.add(b);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}