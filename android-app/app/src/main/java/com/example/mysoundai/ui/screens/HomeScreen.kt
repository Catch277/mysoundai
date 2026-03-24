package com.example.mysoundai.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mysoundai.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val songs = viewModel.songList.value

    LazyColumn {
        item {
            Text(text = "Gợi ý cho bạn", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }
        items(songs) { song ->
            Text(text = "${song.title} - ${song.artist}")
        }
    }
}