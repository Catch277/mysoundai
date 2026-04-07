package com.example.mysoundai.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM downloaded_songs ORDER BY downloadDate DESC")
    fun getAllDownloadedSongs(): Flow<List<DownloadedSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: DownloadedSong)

    @Query("DELETE FROM downloaded_songs WHERE songId = :id")
    suspend fun deleteSongById(id: String)
}