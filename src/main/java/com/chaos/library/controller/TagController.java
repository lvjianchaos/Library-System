package com.chaos.library.controller;

import com.chaos.library.common.errorcode.BaseErrorCode;
import com.chaos.library.common.exception.ClientException;
import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.entity.Tag;
import com.chaos.library.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有标签列表
     * (公开接口，用于前端渲染选项)
     */
    @GetMapping
    public Result<List<Tag>> list() {
        return Results.success(tagService.listAll());
    }

    /**
     * 添加标签 (仅管理员)
     */
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, String> params) {
        checkAdmin();
        String name = params.get("name");
        tagService.addTag(name);
        return Results.success();
    }

    /**
     * 删除标签 (仅管理员)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        tagService.deleteTag(id);
        return Results.success();
    }

    /**
     * 简单的管理员权限检查
     */
    private void checkAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new ClientException(BaseErrorCode.USER_NO_PERMISSION);
        }
    }
}