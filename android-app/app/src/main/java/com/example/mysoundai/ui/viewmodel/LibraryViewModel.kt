package com.example.mysoundai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.data.local.room.PlaylistWithSongs
import com.example.mysoundai.data.local.room.toFavoriteSongEntity
import com.example.mysoundai.data.repository.FavoriteRepository
import com.example.mysoundai.data.repository.PlaylistRepository
import com.example.mysoundai.domain.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val playlistRepository: PlaylistRepository
    ) : ViewModel() {
    val favoriteSongs: StateFlow<List<Song>> = favoriteRepository.getAllFavorites()
        .map { entities -> entities.map { it.toSong() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val playlists: StateFlow<List<PlaylistWithSongs>> = playlistRepository.getAllPlaylistsWithSongs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun isFavorite(songId: String): Flow<Boolean> = favoriteRepository.isFavorite(songId)

    fun toggleFavorite(song: Song, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite)
                favoriteRepository.removeFavorite(song.id)
            else favoriteRepository.addFavorite(song.toFavoriteSongEntity())
        }
    }

    fun createPlaylistWithSongs(name: String, songs: List<Song>) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name).toInt()
            songs.forEach { song ->
                favoriteRepository.addFavorite(song.toFavoriteSongEntity())
                playlistRepository.addSongToPlaylist(playlistId, song.id)
            }
        }
    }

    fun addSongToPlaylistWithCheck(playlistId: Int, song: Song, onAlreadyExists: () -> Unit) {
        viewModelScope.launch {
            val exists = playlistRepository.isSongInPlaylist(playlistId, song.id)
            if (exists) {
                onAlreadyExists()
            }
            favoriteRepository.addFavorite(song.toFavoriteSongEntity())
            playlistRepository.addSongToPlaylist(playlistId, song.id)
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistRepository.deletePlaylistAndCleanUp(playlistId)
        }
    }

    fun getPlaylistDetails(playlistId: Int): Flow<PlaylistWithSongs> {
        return playlistRepository.getPlaylistWithSongs(playlistId)
    }
}