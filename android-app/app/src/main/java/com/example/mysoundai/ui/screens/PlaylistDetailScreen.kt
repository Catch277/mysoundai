package com.example.mysoundai.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.ui.components.SongItem
import com.example.mysoundai.ui.viewmodel.DownloadViewModel
import com.example.mysoundai.ui.viewmodel.LibraryViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    libraryViewModel: LibraryViewModel,
    downloadViewModel: DownloadViewModel,
    playerViewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val playlistWithSongs by libraryViewModel.getPlaylistDetails(playlistId).collectAsState(initial = null)
    val favoriteSongs by libraryViewModel.favoriteSongs.collectAsState()
    val favoriteIds = remember(favoriteSongs) { favoriteSongs.map { it.id }.toSet() }
    val downloadedSongs by downloadViewModel.downloadedSongs.collectAsState()
    val downloadStates by downloadViewModel.downloadStates.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlistWithSongs?.playlist?.name ?: "Playlist", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val songs = playlistWithSongs?.songs?.map { it.toSong() } ?: emptyList()

        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(songs) { song ->
                val isFav = favoriteIds.contains(song.id)
                val localSong = downloadedSongs.find { it.songId == song.id }
                val state = if (localSong != null) {
                    DownloadState.Completed
                } else {
                    downloadStates[song.id] ?: DownloadState.Idle
                }
                SongItem(
                    song = song,
                    isFavorite = isFav,
                    onFavoriteClick = { libraryViewModel.toggleFavorite(song, isFav) },
                    state = state,
                    onDownloadClick = {
                        downloadViewModel.downloadSong(song)
                    },
                    onCancelClick = {
                        downloadViewModel.cancelDownload(song) },
                    onItemClick = {
                        val index = songs.indexOf(song)
                        playerViewModel.playAudioList(songs, index)
                    }
                )
            }
        }
    }
}