package com.example.mysoundai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit,
    state: DownloadState = DownloadState.Idle,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit = {},
    onItemClick: () -> Unit,
) {
    val spotifyGreen = Color(0xFF1DB954)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = song.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 8.dp)
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) spotifyGreen else Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                when (state) {
                    is DownloadState.Idle -> {
                        IconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    is DownloadState.Checking -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                    is DownloadState.Downloading -> {
                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { state.progress / 100f },
                                modifier = Modifier.size(20.dp),
                                color = spotifyGreen,
                                strokeWidth = 2.dp
                            )
                            IconButton(
                                onClick = onCancelClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    is DownloadState.Completed -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Downloaded",
                            tint = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }
                    is DownloadState.Failed -> {
                        IconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    is DownloadState.Cancelled -> {
                        IconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Retry",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}