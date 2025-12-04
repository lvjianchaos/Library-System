package com.chaos.library.service;

import com.chaos.library.common.errorcode.BookErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.entity.Tag;
import com.chaos.library.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    /**
     * 获取所有标签（供前端下拉框使用）
     */
    public List<Tag> listAll() {
        return tagMapper.findAll();
    }

    /**
     * 添加标签
     */
    public void addTag(String name) {
        // 简单查重逻辑，实际建议在数据库 name 字段加唯一索引
        List<Tag> all = tagMapper.findAll();
        boolean exists = all.stream().anyMatch(t -> t.getName().equals(name));
        if (exists) {
            throw new ClientException("标签名称已存在");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
    }

    /**
     * 删除标签
     */
    public void deleteTag(Long id) {
        if (tagMapper.findById(id) == null) {
            throw new ClientException(BookErrorCode.TAG_NOT_FOUND);
        }
        // 注意：删除标签通常会级联删除 book_tag 表中的记录（数据库外键约束 ON DELETE CASCADE）
        tagMapper.deleteById(id);
    }
}