package com.chaos.library.mapper;

import com.chaos.library.dto.BookQuery;
import com.chaos.library.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookMapper {

    @Autowired
    private DataSource dataSource;

    public int insert(Book book) {
        String sql = "INSERT INTO book (title, author, description, isbn, publish_year, cover_url, total, stock, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getDescription());
            ps.setString(4, book.getIsbn());
            if (book.getPublishYear() != null) ps.setInt(5, book.getPublishYear()); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, book.getCoverUrl());
            ps.setInt(7, book.getTotal());
            ps.setInt(8, book.getStock());
            int rows = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) book.setId(rs.getLong(1)); }
            return rows;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int update(Book book) {
        StringBuilder sql = new StringBuilder("UPDATE book SET updated_at = NOW()");
        List<Object> params = new ArrayList<>();
        if (book.getTitle() != null) { sql.append(", title = ?"); params.add(book.getTitle()); }
        if (book.getAuthor() != null) { sql.append(", author = ?"); params.add(book.getAuthor()); }
        if (book.getDescription() != null) { sql.append(", description = ?"); params.add(book.getDescription()); }
        if (book.getPublishYear() != null) { sql.append(", publish_year = ?"); params.add(book.getPublishYear()); }
        if (book.getCoverUrl() != null) { sql.append(", cover_url = ?"); params.add(book.getCoverUrl()); }
        if (book.getStock() != null) { sql.append(", stock = ?"); params.add(book.getStock()); }
        sql.append(" WHERE id = ?");
        params.add(book.getId());
        return executeUpdate(sql.toString(), params.toArray());
    }

    public int updateStock(Long id, int delta) {
        String sql = "UPDATE book SET stock = stock + ?, updated_at = NOW() WHERE id = ? AND (stock + ?) >= 0";
        return executeUpdate(sql, delta, id, delta);
    }

    public int deleteById(Long id) {
        return executeUpdate("DELETE FROM book WHERE id = ?", id);
    }

    public Book findById(Long id) {
        return querySingle("SELECT * FROM book WHERE id = ?", id);
    }

    public Book findByIsbn(String isbn) {
        return querySingle("SELECT * FROM book WHERE isbn = ?", isbn);
    }

    public List<Book> selectList(BookQuery query) {
        StringBuilder sql = new StringBuilder("SELECT b.* FROM book b ");
        List<Object> params = new ArrayList<>();
        buildWhereClause(sql, params, query);
        sql.append(" ORDER BY b.created_at DESC");
        if (query.getLimit() != null && query.getOffset() != null) {
            sql.append(" LIMIT ? OFFSET ?");
            params.add(query.getLimit());
            params.add(query.getOffset());
        }
        return queryList(sql.toString(), params.toArray());
    }

    public long count(BookQuery query) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM book b ");
        List<Object> params = new ArrayList<>();
        buildWhereClause(sql, params, query);
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for(int i=0; i<params.size(); i++) ps.setObject(i+1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getLong(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public void insertBookTag(Long bookId, Long tagId) {
        executeUpdate("INSERT INTO book_tag (book_id, tag_id, created_at) VALUES (?, ?, NOW())", bookId, tagId);
    }

    public void deleteBookTags(Long bookId) {
        executeUpdate("DELETE FROM book_tag WHERE book_id = ?", bookId);
    }

    private void buildWhereClause(StringBuilder sql, List<Object> params, BookQuery query) {
        boolean first = true;
        if (query.getTagName() != null && !query.getTagName().isEmpty()) {
            sql.append(" WHERE EXISTS (SELECT 1 FROM book_tag bt JOIN tag t ON bt.tag_id = t.id WHERE bt.book_id = b.id AND t.name LIKE ?)");
            params.add("%" + query.getTagName() + "%");
            first = false;
        }
        if (query.getTitle() != null && !query.getTitle().isEmpty()) {
            sql.append(first ? " WHERE " : " AND ").append("b.title LIKE ?");
            params.add("%" + query.getTitle() + "%");
            first = false;
        }
        if (query.getAuthor() != null && !query.getAuthor().isEmpty()) {
            sql.append(first ? " WHERE " : " AND ").append("b.author LIKE ?");
            params.add("%" + query.getAuthor() + "%");
            first = false;
        }
        if (query.getIsbn() != null && !query.getIsbn().isEmpty()) {
            sql.append(first ? " WHERE " : " AND ").append("b.isbn = ?");
            params.add(query.getIsbn());
        }
    }

    private int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Book querySingle(String sql, Object... params) {
        List<Book> list = queryList(sql, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Book> queryList(String sql, Object... params) {
        List<Book> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getLong("id"));
                    b.setTitle(rs.getString("title"));
                    b.setAuthor(rs.getString("author"));
                    b.setDescription(rs.getString("description"));
                    b.setIsbn(rs.getString("isbn"));
                    b.setPublishYear(rs.getInt("publish_year"));
                    b.setCoverUrl(rs.getString("cover_url"));
                    b.setTotal(rs.getInt("total"));
                    b.setStock(rs.getInt("stock"));
                    Timestamp t = rs.getTimestamp("created_at");
                    if(t!=null) b.setCreatedAt(t.toLocalDateTime());
                    list.add(b);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // ==========================================
    // 新增实验功能代码：存储过程与触发器
    // ==========================================

    /**
     * 【实验 3.1 存储过程】调用数据库存储过程借书
     * 逻辑：Java 只负责发指令，具体的扣库存、判断触发器状态逻辑都在数据库 proc_borrow_and_get_status 中完成
     *
     * @param bookId 书籍ID
     * @return 数据库返回的操作结果（例如："借阅成功..." 或 "借阅失败..."）
     */
    public String borrowBookWithProc(Long bookId) {
        // {CALL ...} 是 JDBC 调用存储过程的标准语法
        String sql = "{CALL proc_borrow_and_get_status(?, ?)}";
        
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            // 1. 设置输入参数 (IN p_book_id)
            cs.setLong(1, bookId);
            
            // 2. 注册输出参数类型 (OUT p_result_msg)
            // 对应 SQL 中的 VARCHAR
            cs.registerOutParameter(2, Types.VARCHAR);
            
            // 3. 执行存储过程
            cs.execute();
            
            // 4. 获取输出参数的值
            return cs.getString(2);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("存储过程调用失败: " + e.getMessage());
        }
    }

    /**
     * 【实验 3.2 触发器验证】查询图书监控状态
     * 逻辑：这个状态不是 Java 写入的，而是当库存变化时，触发器 trg_auto_update_status 自动写入的
     * 注意：请确保数据库中已创建表 book_status_monitor
     */
    public String checkBookMonitorStatus(Long bookId) {
        String sql = "SELECT display_status FROM book_status_monitor WHERE book_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, bookId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("display_status");
                }
            }
        } catch (SQLException e) {
            // 如果表不存在或查询失败
            System.err.println("触发器监控表查询失败（可能是表未创建）: " + e.getMessage());
        }
        return "暂无状态记录";
    }

    


}