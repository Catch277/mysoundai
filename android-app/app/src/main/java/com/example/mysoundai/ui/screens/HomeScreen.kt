package com.example.mysoundai.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.mysoundai.ui.components.SongItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mysoundai.ui.components.DynamicGradientBox
import com.example.mysoundai.ui.components.SongCard

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val songs = viewModel.songList.value
    val isLoading = viewModel.isLoading.value

    val firstSongImageUrl = songs.firstOrNull()?.imageUrl ?: ""
    DynamicGradientBox(imageUrl = firstSongImageUrl, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HomeHeader()
                }
                item {
                    HomeRowTitle("Playlist nổi bật")
                    LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(songs) { song ->
                            SongCard(song = song)
                    }
                }
            }
                item {
                    HomeRowTitle("Gợi ý cho bạn")
                }
                items(songs) { song ->
                    SongItem(song = song)
                }
        }
    }
}
}
@Composable
fun HomeHeader() {
    Text(text = "Chào buổi tối",
        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
        color = androidx.compose.ui.graphics.Color.White,
        modifier = Modifier.padding(bottom = 16.dp))
}
@Composable
fun HomeRowTitle(title: String) {
    Text(text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp))
}