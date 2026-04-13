package com.example.mysoundai.data.repository

import com.example.mysoundai.data.local.room.FavoriteDao
import com.example.mysoundai.data.local.room.FavoriteSongEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    fun getAllFavorites() = favoriteDao.getAllFavorites()

    fun isFavorite(songId: String): Flow<Boolean> = favoriteDao.isFavorite(songId)

    suspend fun addFavorite(entity: FavoriteSongEntity) = favoriteDao.insertFavorite(entity)

    suspend fun removeFavorite(songId: String) = favoriteDao.deleteFavorite(songId)
}