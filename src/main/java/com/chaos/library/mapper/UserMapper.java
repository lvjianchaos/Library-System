package com.chaos.library.mapper;

import com.chaos.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    int insert(User user);

    int update(User user);

    User findById(Long id);
}