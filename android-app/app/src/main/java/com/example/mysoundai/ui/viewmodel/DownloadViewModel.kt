package com.example.mysoundai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.R
import com.example.mysoundai.core.util.UiText
import com.example.mysoundai.data.local.room.DownloadedSong
import com.example.mysoundai.data.repository.DownloadRepository
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.domain.model.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.toMutableMap

sealed class DownloadUiEvent {
    data class ShowToast(val message: UiText): DownloadUiEvent()
}

class DownloadViewModel(private val repository: DownloadRepository) : ViewModel() {

    private val _downloadStates = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadState>> = _downloadStates
    val downloadedSongs: StateFlow<List<DownloadedSong>> = repository.allDownloadedSongs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _uiEvent = Channel<DownloadUiEvent>()
    private val downloadJobs = mutableMapOf<String, Job>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun downloadSong(song: Song) {
        val currentStates = _downloadStates.value[song.id]
        if (currentStates is DownloadState.Downloading) return
        if (currentStates is DownloadState.Completed) return
        val job = viewModelScope.launch {
            val isDownloaded = repository.isDownloaded(song.id)
            if (isDownloaded) {
                _downloadStates.update {
                    it + (song.id to DownloadState.Completed)
                }
                _uiEvent.send(
                    DownloadUiEvent.ShowToast(
                        UiText.StringResource(R.string.msg_song_already_exists)
                    )
                )
                return@launch
            }
            repository.downloadSong(song)
                .collect { state ->
                    _downloadStates.update {
                        it + (song.id to state)
                    }
                    when (state) {
                        is DownloadState.Completed -> {
                            _uiEvent.send(
                                DownloadUiEvent.ShowToast(
                                    UiText.StringResource(R.string.msg_download_success, song.title)
                                )
                            )
                        }

                        is DownloadState.Failed -> {
                            _uiEvent.send(
                                DownloadUiEvent.ShowToast(
                                    UiText.StringResource(
                                        R.string.msg_download_failed,
                                        song.title
                                    )
                                )
                            )
                        }

                        else -> {}
                    }
                }
        }
        downloadJobs[song.id] = job
    }

    fun deleteDownloadedSong(song: DownloadedSong) {
        viewModelScope.launch {
            repository.deleteSong(song)
            _downloadStates.update { currentMap ->
                val mutableMap = currentMap.toMutableMap()
                mutableMap.remove(song.songId)
                mutableMap
            }
            _uiEvent.send(DownloadUiEvent.ShowToast(
                UiText.StringResource(R.string.msg_song_deleted, song.title)
            ))
        }
    }

    fun cancelDownload(song: Song) {
        val job = downloadJobs[song.id]
        if (job != null && job.isActive) {
            job.cancel()
            downloadJobs.remove(song.id)
            _downloadStates.update { currentMap ->
                currentMap + (song.id to DownloadState.Cancelled)
            }
            viewModelScope.launch {
                _uiEvent.send(DownloadUiEvent.ShowToast(
                    UiText.StringResource(R.string.msg_download_cancelled, song.title)
                ))
            }
        }
    }

    fun resetCancelledStates() {
        _downloadStates.update { currentMap ->
            currentMap.mapValues { (_, state) ->
                if (state is DownloadState.Cancelled)
                    DownloadState.Idle
                else state
            }
        }
    }
    fun resetFailedStates() {
        _downloadStates.update { currentMap ->
            currentMap.mapValues { (_, state) ->
                if (state is DownloadState.Failed)
                    DownloadState.Idle
                else state
            }
        }
    }
}