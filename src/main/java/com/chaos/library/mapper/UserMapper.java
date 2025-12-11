package com.chaos.library.mapper;

import com.chaos.library.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserMapper {

    @Autowired
    private DataSource dataSource;

    public User findByUsername(String username) {
        return querySingle("SELECT * FROM `user` WHERE username = ?", username);
    }

    public User findById(Long id) {
        return querySingle("SELECT * FROM `user` WHERE id = ?", id);
    }

    public int insert(User user) {
        String sql = "INSERT INTO `user` (username, password, role, name, phone, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getName());
            ps.setString(5, user.getPhone());

            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getLong(1));
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(User user) {
        StringBuilder sql = new StringBuilder("UPDATE `user` SET updated_at = NOW()");
        List<Object> params = new ArrayList<>();

        if (user.getPassword() != null) { sql.append(", password = ?"); params.add(user.getPassword()); }
        if (user.getName() != null) { sql.append(", name = ?"); params.add(user.getName()); }
        if (user.getPhone() != null) { sql.append(", phone = ?"); params.add(user.getPhone()); }

        sql.append(" WHERE id = ?");
        params.add(user.getId());

        return executeUpdate(sql.toString(), params.toArray());
    }

    // --- 辅助方法 ---

    private User querySingle(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setName(rs.getString("name"));
        user.setPhone(rs.getString("phone"));
        Timestamp t = rs.getTimestamp("created_at");
        if(t != null) user.setCreatedAt(t.toLocalDateTime());
        return user;
    }
}