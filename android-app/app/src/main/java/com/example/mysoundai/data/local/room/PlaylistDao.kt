package com.example.mysoundai.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_song_cross_ref WHERE playlistId = :pId AND songId = :sId)")
    suspend fun isSongInPlaylist(pId: Int, sId: String): Boolean

    @Transaction
    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistWithSongs(playlistId: Int): Flow<PlaylistWithSongs>

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int)

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteCrossRefsByPlaylist(playlistId: Int)

    @Transaction
    suspend fun deletePlaylistAndCleanUp(playlistId: Int) {
        deleteCrossRefsByPlaylist(playlistId)
        deletePlaylistById(playlistId)
    }
}