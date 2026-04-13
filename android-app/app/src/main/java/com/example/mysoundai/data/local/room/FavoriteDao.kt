package com.example.mysoundai.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(song: FavoriteSongEntity)

    @Query("DELETE FROM favorite_songs WHERE songId = :songId")
    suspend fun deleteFavorite(songId: String)

    @Query("SELECT * FROM favorite_songs")
    fun getAllFavorites(): Flow<List<FavoriteSongEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :songId)")
    fun isFavorite(songId: String): Flow<Boolean>
}

