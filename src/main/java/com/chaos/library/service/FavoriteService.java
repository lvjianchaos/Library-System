package com.chaos.library.service;

import com.chaos.library.common.exception.ClientException;
import com.chaos.library.entity.Favorite;
import com.chaos.library.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public void addFavorite(Long userId, Long bookId) {
        // 1. 检查是否已收藏
        Favorite exist = favoriteMapper.findByUserIdAndBookId(userId, bookId);
        if (exist != null) {
            throw new ClientException("您已收藏过该书");
        }
        // 2. 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long bookId) {
        favoriteMapper.deleteByUserIdAndBookId(userId, bookId);
    }

    public List<Favorite> getMyFavorites(Long userId) {
        return favoriteMapper.findListByUserId(userId);
    }
}