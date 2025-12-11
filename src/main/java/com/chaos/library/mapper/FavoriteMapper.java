package com.chaos.library.mapper;

import com.chaos.library.entity.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FavoriteMapper {

    @Autowired
    private DataSource dataSource;

    public int insert(Favorite favorite) {
        String sql = "INSERT INTO favorite (user_id, book_id, created_at) VALUES (?, ?, NOW())";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, favorite.getUserId());
            ps.setLong(2, favorite.getBookId());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if(rs.next()) favorite.setId(rs.getLong(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int deleteByUserIdAndBookId(Long userId, Long bookId) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM favorite WHERE user_id = ? AND book_id = ?")) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Favorite findByUserIdAndBookId(Long userId, Long bookId) {
        String sql = "SELECT * FROM favorite WHERE user_id = ? AND book_id = ? LIMIT 1";
        return querySingle(sql, userId, bookId);
    }

    public List<Favorite> findListByUserId(Long userId) {
        String sql = "SELECT f.id, f.user_id, f.book_id, f.created_at, b.title as bookTitle, b.author as bookAuthor, b.cover_url as bookCoverUrl FROM favorite f LEFT JOIN book b ON f.book_id = b.id WHERE f.user_id = ? ORDER BY f.created_at DESC";
        return queryList(sql, userId);
    }

    private Favorite querySingle(String sql, Object... params) {
        List<Favorite> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Favorite> queryList(String sql, Object... params) {
        List<Favorite> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Favorite f = new Favorite();
                    f.setId(rs.getLong("id"));
                    f.setUserId(rs.getLong("user_id"));
                    f.setBookId(rs.getLong("book_id"));
                    Timestamp t = rs.getTimestamp("created_at");
                    if(t!=null) f.setCreatedAt(t.toLocalDateTime());
                    try { f.setBookTitle(rs.getString("bookTitle")); } catch(SQLException ignored){}
                    try { f.setBookAuthor(rs.getString("bookAuthor")); } catch(SQLException ignored){}
                    try { f.setBookCoverUrl(rs.getString("bookCoverUrl")); } catch(SQLException ignored){}
                    list.add(f);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}