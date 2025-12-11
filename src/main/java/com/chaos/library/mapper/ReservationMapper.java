package com.chaos.library.mapper;

import com.chaos.library.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationMapper {

    @Autowired
    private DataSource dataSource;

    public int insert(Reservation reservation) {
        String sql = "INSERT INTO reservation (user_id, book_id, status, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, reservation.getUserId());
            ps.setLong(2, reservation.getBookId());
            ps.setInt(3, reservation.getStatus());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if(rs.next()) reservation.setId(rs.getLong(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int updateStatus(Long id, Integer status) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE reservation SET status = ? WHERE id = ?")) {
            ps.setInt(1, status);
            ps.setLong(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Reservation findFirstPendingReservation(Long bookId) {
        return querySingle("SELECT * FROM reservation WHERE book_id = ? AND status = 0 ORDER BY created_at ASC LIMIT 1", bookId);
    }

    public Reservation findPendingByUserIdAndBookId(Long userId, Long bookId) {
        return querySingle("SELECT * FROM reservation WHERE user_id = ? AND book_id = ? AND status = 0 LIMIT 1", userId, bookId);
    }

    public List<Reservation> findListByUserId(Long userId) {
        String sql = "SELECT r.*, b.title as bookTitle FROM reservation r LEFT JOIN book b ON r.book_id = b.id WHERE r.user_id = ? ORDER BY r.created_at DESC";
        return queryList(sql, userId);
    }

    private Reservation querySingle(String sql, Object... params) {
        List<Reservation> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Reservation> queryList(String sql, Object... params) {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getLong("id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setBookId(rs.getLong("book_id"));
                    r.setStatus(rs.getInt("status"));
                    Timestamp t = rs.getTimestamp("created_at");
                    if(t!=null) r.setCreatedAt(t.toLocalDateTime());
                    try { r.setBookTitle(rs.getString("bookTitle")); } catch(SQLException ignored){}
                    list.add(r);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}