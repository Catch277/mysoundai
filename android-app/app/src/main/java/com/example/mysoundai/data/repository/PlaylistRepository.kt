package com.example.mysoundai.data.repository

import com.example.mysoundai.data.local.room.PlaylistDao
import com.example.mysoundai.data.local.room.PlaylistEntity
import com.example.mysoundai.data.local.room.PlaylistSongCrossRef
import com.example.mysoundai.data.local.room.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.createPlaylist(PlaylistEntity(name = name))
    }

    suspend fun addSongToPlaylist(playlistId: Int, songId: String) {
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId))
    }

    suspend fun isSongInPlaylist(playlistId: Int, songId: String): Boolean {
        return playlistDao.isSongInPlaylist(playlistId, songId)
    }

    suspend fun getPlaylistDetails(playlistId: Int): Flow<PlaylistWithSongs> {
        return playlistDao.getPlaylistWithSongs(playlistId)
    }

    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>> {
        return playlistDao.getAllPlaylistsWithSongs()
    }

    fun getPlaylistWithSongs(playlistId: Int): Flow<PlaylistWithSongs> {
        return playlistDao.getPlaylistWithSongs(playlistId)
    }

    suspend fun deletePlaylistAndCleanUp(playlistId: Int) {
        playlistDao.deletePlaylistAndCleanUp(playlistId)
    }
}