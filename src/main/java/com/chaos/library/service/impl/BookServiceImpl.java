package com.chaos.library.service.impl;

import com.chaos.library.common.errorcode.BookErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.common.result.PageResult;
import com.chaos.library.dto.BookDto;
import com.chaos.library.dto.BookQuery;
import com.chaos.library.dto.BookSaveRequest;
import com.chaos.library.entity.Book;
import com.chaos.library.entity.Tag;
import com.chaos.library.mapper.BookMapper;
import com.chaos.library.mapper.TagMapper;
import com.chaos.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final TagMapper tagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBook(BookSaveRequest request) {
        // 1. 校验ISBN是否存在
        if (bookMapper.findByIsbn(request.getIsbn()) != null) {
            throw new ClientException(BookErrorCode.ISBN_EXIST);
        }

        // 2. 保存图书基本信息
        Book book = new Book();
        BeanUtils.copyProperties(request, book);
        book.setStock(request.getTotal()); // 初始库存 = 总数
        bookMapper.insert(book);

        // 3. 关联标签
        if (!CollectionUtils.isEmpty(request.getTagIds())) {
            for (Long tagId : request.getTagIds()) {
                // 简单校验tag是否存在，或者直接插入（如果外键约束会报错）
                // 这里假设前端传来的tagId都是有效的
                bookMapper.insertBookTag(book.getId(), tagId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Long id, BookSaveRequest request) {
        Book existBook = bookMapper.findById(id);
        if (existBook == null) {
            throw new ClientException(BookErrorCode.BOOK_NOT_FOUND);
        }

        // 1. 校验ISBN是否与其他书冲突
        Book isbnBook = bookMapper.findByIsbn(request.getIsbn());
        if (isbnBook != null && !isbnBook.getId().equals(id)) {
            throw new ClientException(BookErrorCode.ISBN_EXIST);
        }

        // 2. 更新基本信息
        int diff = request.getTotal() - existBook.getTotal();
        BeanUtils.copyProperties(request, existBook);
        // 注意：修改总数时，库存逻辑比较复杂。
        // 简单策略：库存变化量 = 新总数 - 旧总数
        // 新库存 = 旧库存 + (新总数 - 旧总数)
        existBook.setStock(existBook.getStock() + diff);
        if (existBook.getStock() < 0) {
            throw new ClientException("修改失败，减少的数量超过了当前库存量");
        }

        bookMapper.update(existBook);

        // 3. 更新标签 (先删后加)
        if (request.getTagIds() != null) {
            bookMapper.deleteBookTags(id);
            for (Long tagId : request.getTagIds()) {
                bookMapper.insertBookTag(id, tagId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        if (bookMapper.findById(id) == null) {
            throw new ClientException(BookErrorCode.BOOK_NOT_FOUND);
        }
        // 由于数据库设置了 ON DELETE CASCADE，删除 Book 会自动删除 book_tag 记录
        // 但如果有关联的借阅记录(borrow)，可能会受外键约束限制，这里暂不处理借阅冲突
        bookMapper.deleteById(id);
    }

    @Override
    public BookDto getBookById(Long id) {
        // 1. 查基本信息
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new ClientException(BookErrorCode.BOOK_NOT_FOUND);
        }

        // 2. 查关联标签
        List<Tag> tags = tagMapper.findByBookId(id);

        // 3. 组装 DTO
        BookDto dto = new BookDto();
        BeanUtils.copyProperties(book, dto);
        dto.setTags(tags);

        return dto;
    }

    @Override
    public PageResult<Book> getBookList(BookQuery query) {
        // 如果没有传分页参数，给个默认值防止全表查
        if (query.getLimit() == null) query.setLimit(20);
        if (query.getOffset() == null) query.setOffset(0);

        List<Book> list = bookMapper.selectList(query);
        long total = bookMapper.count(query);

        return new PageResult<>(list, total);
    }
}