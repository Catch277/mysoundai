package com.example.mysoundai.ui.components

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
             onDownloadClick: () -> Unit,
             onCancelClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                        Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.retry_download))
                    }
                }
            }
        }
    }
}