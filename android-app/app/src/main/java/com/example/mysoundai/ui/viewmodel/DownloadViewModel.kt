package com.example.mysoundai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.data.local.room.DownloadedSong
import com.example.mysoundai.data.repository.DownloadRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloadViewModel(private val repository: DownloadRepository) : ViewModel() {

    // Danh sách nhạc sẽ tự động cập nhật khi Database thay đổi
    val downloadedSongs: StateFlow<List<DownloadedSong>> = repository.allDownloadedSongs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Hàm này sẽ được gọi khi user bấm nút tải nhạc ở Home hoặc Trình phát nhạc
    fun downloadSong(songId: String, title: String, artist: String, duration: Long) {
        viewModelScope.launch {
            repository.saveDownloadedSong(songId, title, artist, duration)
        }
    }
}