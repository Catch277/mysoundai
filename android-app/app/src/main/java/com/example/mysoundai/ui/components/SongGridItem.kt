package com.example.mysoundai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mysoundai.R
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.domain.model.Song

@Composable
fun SongGridItem(
    modifier: Modifier = Modifier,
    song: Song,
    state: DownloadState = DownloadState.Idle,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit = {},
    onItemClick: () -> Unit,
) {
    val isDownloaded = state is DownloadState.Completed

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = song.imageUrl,
                contentDescription = "Cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = 6.dp,
                        end = 8.dp
                    )
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
                        Box(
                            contentAlignment = Alignment.Center) {
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
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, false)
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, false)
                )
            }
        }
    }
}