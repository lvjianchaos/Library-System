package com.chaos.library.mapper;

import com.chaos.library.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TagMapper {

    @Autowired
    private DataSource dataSource;

    public int insert(Tag tag) {
        String sql = "INSERT INTO tag (name, created_at) VALUES (?, NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tag.getName());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) tag.setId(rs.getLong(1));
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteById(Long id) {
        return executeUpdate("DELETE FROM tag WHERE id = ?", id);
    }

    public Tag findById(Long id) {
        return querySingle("SELECT * FROM tag WHERE id = ?", id);
    }

    public List<Tag> findAll() {
        return queryList("SELECT * FROM tag ORDER BY id DESC");
    }

    public List<Tag> findByBookId(Long bookId) {
        String sql = "SELECT t.* FROM tag t INNER JOIN book_tag bt ON t.id = bt.tag_id WHERE bt.book_id = ?";
        return queryList(sql, bookId);
    }

    // --- 辅助方法 ---
    private int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Tag querySingle(String sql, Object... params) {
        List<Tag> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Tag> queryList(String sql, Object... params) {
        List<Tag> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tag t = new Tag();
                    t.setId(rs.getLong("id"));
                    t.setName(rs.getString("name"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if(ts!=null) t.setCreatedAt(ts.toLocalDateTime());
                    list.add(t);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}