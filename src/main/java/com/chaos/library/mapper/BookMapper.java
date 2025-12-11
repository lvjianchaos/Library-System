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
}