package com.example.mysoundai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.domain.model.Song
import com.example.mysoundai.service.MusicController
import com.example.mysoundai.service.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val musicController: MusicController
) : ViewModel() {

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> get() = _currentSong

    private var currentPlaylist: List<Song> = emptyList()

    init {
        viewModelScope.launch {
            musicController.getPlaybackState().collect { state ->
                _playbackState.value = state

                if (state.currentMediaId.isNotEmpty() && state.currentMediaId != _currentSong.value?.id) {
                    val nextSong = currentPlaylist.find { it.id == state.currentMediaId }
                    if (nextSong != null) {
                        _currentSong.value = nextSong
                    }
                }
            }
        }
    }
    fun playAudio(song: Song, isOffline: Boolean = false, localPath: String? = null) {
        val uriToPlay = if (isOffline && localPath != null) {
            localPath
        } else {
            song.audioUrl ?: return
        }
        _currentSong.value = song
        musicController.playSong(song, uriToPlay)
    }

    fun playAudioList(songs: List<Song>, startIndex: Int, localPaths: Map<String, String> = emptyMap()) {
        currentPlaylist = songs
        if (songs.isNotEmpty() && startIndex in songs.indices) {
            _currentSong.value = songs[startIndex]
        }
        musicController.playPlayList(songs, startIndex, localPaths)
    }

    fun seekTo(position: Long) {
        val duration = _playbackState.value.duration
        if (position >= 0) {
            val safePosition = position.coerceIn(0L, duration)
            musicController.seekTo(safePosition)
        }
    }

    fun setShuffleMode(enabled: Boolean) {
        musicController.toggleShuffle(enabled)
    }

    fun setRepeatMode() {
        musicController.toggleRepeatMode()
    }

    fun skipNext() = musicController.skipToNext()

    fun skipPrevious() = musicController.skipToPrevious()

    fun pause() {
        musicController.pause()
    }

    fun resume() {
        musicController.resume()
    }

    override fun onCleared() {
        super.onCleared()
        musicController.release()
    }
}