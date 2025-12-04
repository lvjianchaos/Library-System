package com.chaos.library.controller;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.common.result.PageResult;
import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.dto.BookDto;
import com.chaos.library.dto.BookQuery;
import com.chaos.library.dto.BookSaveRequest;
import com.chaos.library.entity.Book;
import com.chaos.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 获取图书列表 (公开或需登录，视SecurityConfig而定)
     */
    @GetMapping
    public Result<PageResult<Book>> list(BookQuery query) {
        return Results.success(bookService.getBookList(query));
    }

    /**
     * 获取单个图书详情
     */
    @GetMapping("/{id}")
    public Result<BookDto> detail(@PathVariable Long id) {
        return Results.success(bookService.getBookById(id));
    }

    /**
     * 创建图书 (仅管理员)
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid BookSaveRequest request) {
        checkAdmin();
        bookService.createBook(request);
        return Results.success();
    }

    /**
     * 更新图书 (仅管理员)
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid BookSaveRequest request) {
        checkAdmin();
        bookService.updateBook(id, request);
        return Results.success();
    }

    /**
     * 删除图书 (仅管理员)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        bookService.deleteBook(id);
        return Results.success();
    }

    /**
     * 简单的权限辅助检查
     * 如果 SecurityConfig 未开启 @EnableMethodSecurity，可手动检查
     */
    private void checkAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 角色我们在 UserDetailsServiceImpl 中存的是 "ROLE_ADMIN" 或 "ROLE_STUDENT"
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new ClientException(BaseErrorCode.USER_NO_PERMISSION);
        }
    }
}