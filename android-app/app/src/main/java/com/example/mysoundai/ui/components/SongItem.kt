package com.example.mysoundai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mysoundai.domain.model.Song
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.example.mysoundai.R
import com.example.mysoundai.domain.model.DownloadState

@Composable
fun SongItem(song: Song,
             state: DownloadState = DownloadState.Idle,
             isFavorite: Boolean = false,
             onFavoriteClick: () -> Unit,
             onDownloadClick: () -> Unit,
             onCancelClick: () -> Unit = {},
             onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onItemClick() }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onItemClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.imageUrl,
                null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = song.title, style = MaterialTheme.typography.titleMedium)
                Text(text = song.artist, style = MaterialTheme.typography.titleMedium)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFF1DB954) else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        is DownloadState.Idle -> {
                            IconButton(onClick = onDownloadClick) {
                                Icon(Icons.Default.Download, contentDescription = null)
                            }
                        }

                        is DownloadState.Checking -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        is DownloadState.Downloading -> {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { state.progress / 100f },
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                                IconButton(
                                    onClick = onCancelClick,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        is DownloadState.Completed -> {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }

                        is DownloadState.Failed -> {
                            IconButton(onClick = onDownloadClick) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                            }
                        }

                        is DownloadState.Cancelled -> {
                            IconButton(onClick = onDownloadClick) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = stringResource(R.string.retry_download)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}