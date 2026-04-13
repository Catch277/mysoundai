package com.example.mysoundai.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mysoundai.domain.model.Song
import com.example.mysoundai.ui.viewmodel.DownloadViewModel
import com.example.mysoundai.ui.viewmodel.LibraryViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import com.example.mysoundai.ui.viewmodel.HomeViewModel

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    downloadViewModel: DownloadViewModel,
    homeViewModel: HomeViewModel,
    onNavigateToDownloads: () -> Unit,
    onNavigateToPlaylistDetail: (Int) -> Unit
) {
    val favoriteSongs by libraryViewModel.favoriteSongs.collectAsState()
    val downloadedSongs by downloadViewModel.downloadedSongs.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val spotifyGreen = Color(0xFF1DB954)

    val playlists by libraryViewModel.playlists.collectAsState()
    val allSongs = homeViewModel.songList.value


    var showCreateDialog by remember { mutableStateOf(false) }


    Scaffold(
        containerColor = colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Thư viện của bạn",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    LibraryItem(
                        title = "Bài hát đã thích",
                        subtitle = "${favoriteSongs.size} bài hát",
                        icon = Icons.Default.Favorite,
                        iconTint = spotifyGreen,
                        hasContent = favoriteSongs.isNotEmpty(),
                        onClick = {
                            if (favoriteSongs.isNotEmpty()) {
                                playerViewModel.playAudioList(favoriteSongs, 0)
                            }
                        }
                    )
                }
                item {
                    LibraryItem(
                        title = "Nhạc đã tải",
                        subtitle = "${downloadedSongs.size} bài hát",
                        icon = Icons.Default.DownloadForOffline,
                        iconTint = spotifyGreen,
                        hasContent = downloadedSongs.isNotEmpty(),
                        onClick = onNavigateToDownloads
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Playlist của tôi",
                        style = MaterialTheme.typography.titleLarge,
                        color = colorScheme.onBackground,
                    )
                }
                item {
                    if (playlists.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        ) {
                            Text(
                                text = "Bạn chưa tạo playlist nào. Hãy cá nhân hóa trải nghiệm của bạn bằng Playlist cá nhân",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                items(playlists) { playlistWithSongs ->
                    LibraryItem(
                        title = playlistWithSongs.playlist.name,
                        subtitle = "${playlistWithSongs.songs.size} bài hát",
                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                        iconTint = spotifyGreen,
                        hasContent = playlistWithSongs.songs.isNotEmpty(),
                        onClick = {
                            onNavigateToPlaylistDetail(playlistWithSongs.playlist.id)
                        },
                        onDeleteClick = {
                            libraryViewModel.deletePlaylist(playlistWithSongs.playlist.id)
                        }
                    )
                }
            }
        }
    }
    if (showCreateDialog) {
        CreatePlaylistDialog(
            allSongs = allSongs,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, selectedSongs ->
                libraryViewModel.createPlaylistWithSongs(name, selectedSongs)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun LibraryItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    hasContent: Boolean,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val spotifyGreen = Color(0xFF1DB954)


    val dotColor by animateColorAsState(
        targetValue = if (hasContent) spotifyGreen
        else colorScheme.outline.copy(alpha = 0.5f),
        label = ""
    )

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = ""
    )

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            pressed = when (interaction) {
                is PressInteraction.Press -> true
                is PressInteraction.Release,
                is PressInteraction.Cancel -> false
                else -> false
            }
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
                onClickLabel = null
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            if (onDeleteClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa playlist",
                        tint = colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(dotColor, shape = RoundedCornerShape(50))
                )
            }
        }
    }
}

// 3. The dialog composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    allSongs: List<Song>,
    onDismiss: () -> Unit,
    onCreate: (name: String, songs: List<Song>) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    val selectedSongs = remember { mutableStateListOf<Song>() }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Tạo Playlist mới",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Tên playlist") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Chọn bài hát", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(allSongs) { song ->
                    val checked = song in selectedSongs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (checked) selectedSongs.remove(song)
                                else selectedSongs.add(song)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                if (it) selectedSongs.add(song)
                                else selectedSongs.remove(song)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(song.title, style = MaterialTheme.typography.bodyLarge)
                            Text(song.artist, style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onCreate(playlistName.trim(), selectedSongs.toList()) },
                enabled = playlistName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Tạo", color = Color.Black)
            }
        }
    }
}


