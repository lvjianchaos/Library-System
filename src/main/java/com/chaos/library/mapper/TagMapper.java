package com.chaos.library.mapper;

import com.chaos.library.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    int insert(Tag tag);

    int deleteById(Long id);

    Tag findById(Long id);

    List<Tag> findAll();

    // 根据图书ID查找标签
    List<Tag> findByBookId(Long bookId);
}