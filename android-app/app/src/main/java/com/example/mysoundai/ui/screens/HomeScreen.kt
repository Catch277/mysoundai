package com.example.mysoundai.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mysoundai.ui.components.SongItem

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val songs = viewModel.songList.value
    val isLoading = viewModel.isLoading.value

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Gợi ý cho bạn",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                items(songs) { song ->
                    SongItem(song = song)
                }
            }
        }
    }
}