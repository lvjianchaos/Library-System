package com.chaos.library.mapper;

import com.chaos.library.dto.BookQuery;
import com.chaos.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {

    int insert(Book book);

    int update(Book book);

    int deleteById(Long id);

    Book findById(Long id);

    Book findByIsbn(String isbn);

    // 复杂查询：列表
    List<Book> selectList(@Param("query") BookQuery query);

    // 复杂查询：统计总数
    long count(@Param("query") BookQuery query);

    // 维护中间表：添加关联
    void insertBookTag(@Param("bookId") Long bookId, @Param("tagId") Long tagId);

    // 维护中间表：删除某本书的所有关联
    void deleteBookTags(Long bookId);

    // 库存操作
    int updateStock(@Param("id") Long id, @Param("delta") int delta);
}