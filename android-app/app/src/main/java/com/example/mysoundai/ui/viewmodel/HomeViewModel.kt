package com.example.mysoundai.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var searchQuery by mutableStateOf("")
    val filteredSongs by derivedStateOf {
        if (searchQuery.isEmpty()) {
            songList.value
        } else {
            songList.value.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true)
            }
        }
    }
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
    fun onSearchQueryChanged(newQuery: String) {
        searchQuery = newQuery
    }
}
