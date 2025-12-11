package com.chaos.library.mapper;

import com.chaos.library.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentMapper {

    @Autowired
    private DataSource dataSource;

    public int insert(Comment comment) {
        String sql = "INSERT INTO comment (user_id, book_id, content, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, comment.getUserId());
            ps.setLong(2, comment.getBookId());
            ps.setString(3, comment.getContent());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) comment.setId(rs.getLong(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int deleteById(Long id) {
        return executeUpdate("DELETE FROM comment WHERE id = ?", id);
    }

    public Comment findById(Long id) {
        return querySingle("SELECT * FROM comment WHERE id = ?", id);
    }

    public List<Comment> findByBookId(Long bookId) {
        // 关联用户和书名
        String sql = "SELECT c.*, u.name as userName, b.title as bookTitle FROM comment c LEFT JOIN `user` u ON c.user_id = u.id LEFT JOIN book b ON c.book_id = b.id WHERE c.book_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, bookId);
    }

    public List<Comment> findByUserId(Long userId) {
        String sql = "SELECT c.*, b.title as bookTitle FROM comment c LEFT JOIN book b ON c.book_id = b.id WHERE c.user_id = ? ORDER BY c.created_at DESC";
        return queryList(sql, userId);
    }

    private int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Comment querySingle(String sql, Object... params) {
        List<Comment> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Comment> queryList(String sql, Object... params) {
        List<Comment> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for(int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getLong("id"));
                    c.setUserId(rs.getLong("user_id"));
                    c.setBookId(rs.getLong("book_id"));
                    c.setContent(rs.getString("content"));
                    Timestamp t = rs.getTimestamp("created_at");
                    if(t!=null) c.setCreatedAt(t.toLocalDateTime());
                    try {
                        c.setUserName(rs.getString("userName"));
                    } catch(SQLException ignored){}
                    try {
                        c.setBookTitle(rs.getString("bookTitle"));
                    } catch(SQLException ignored){}
                    list.add(c);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}