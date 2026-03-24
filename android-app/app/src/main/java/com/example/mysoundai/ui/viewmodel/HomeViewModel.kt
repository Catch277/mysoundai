package com.example.mysoundai.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.di.NetworkModule
import com.example.mysoundai.domain.model.Song
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var songList = mutableStateOf<List<Song>>(emptyList())
    var isLoading = mutableStateOf(false)
    init {
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = NetworkModule.apiService.getRecomendations()
                songList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }


}