package com.example.mysoundai.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.di.NetworkModule
import com.example.mysoundai.domain.model.Song
import com.example.mysoundai.domain.repository.MusicRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {
    var songList = mutableStateOf<List<Song>>(emptyList())
    var isLoading = mutableStateOf(false)
    init {
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = musicRepository.getTrendingSongs()
                songList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }


}