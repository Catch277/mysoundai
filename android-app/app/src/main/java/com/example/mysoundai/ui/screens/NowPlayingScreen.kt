package com.example.mysoundai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mysoundai.ui.viewmodel.LibraryViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel


@Composable
fun NowPlayingScreen(
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    onClose: () -> Unit
) {
    val currentSong by playerViewModel.currentSong.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    val repeatMode = playbackState.repeatMode
    val isShuffled = playbackState.isShuffleEnabled

    val isFavorite by libraryViewModel.isFavorite(currentSong?.id ?: "")
        .collectAsState(initial = false)

    val colorScheme = MaterialTheme.colorScheme


    var rotation by remember { mutableFloatStateOf(0f) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(playbackState.isPlaying) {
        var lastFrame = 0L
        while (playbackState.isPlaying) {
            withFrameMillis { frame ->
                if (lastFrame > 0L) {
                    rotation = (rotation + (frame - lastFrame) * 0.024f) % 360f
                }
                lastFrame = frame
            }
        }
    }

    LaunchedEffect(playbackState.currentPosition, playbackState.duration) {
        if (!isDragging && playbackState.duration > 0) {
            sliderPosition = playbackState.currentPosition.toFloat() / playbackState.duration
        }
    }

    if (currentSong == null) return

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures { } }
                .background(Color(0XFF121212))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Close",
                    tint = colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentSong?.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .rotate(rotation),
                    placeholder = ColorPainter(Color.DarkGray),
                    error = ColorPainter(Color.DarkGray)
                )
            }
                Spacer(modifier = Modifier.height(48.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentSong?.title ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentSong?.artist ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                IconButton(onClick = {
                    currentSong?.let { libraryViewModel.toggleFavorite(it, isFavorite) }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) colorScheme.primary else colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = sliderPosition.coerceIn(0f, 1f),
                    onValueChange = {
                        isDragging = true
                        sliderPosition = it
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        val seekTime = (sliderPosition * playbackState.duration).toLong()
                        playerViewModel.seekTo(seekTime)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = colorScheme.primary,
                        activeTrackColor = colorScheme.primary,
                        inactiveTrackColor = colorScheme.surfaceVariant
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(playbackState.currentPosition),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDuration(playbackState.duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { playerViewModel.setShuffleMode(!isShuffled) }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffled) colorScheme.primary else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = { playerViewModel.skipPrevious() }) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = colorScheme.onBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    val scale by animateFloatAsState(
                        targetValue = if (playbackState.isPlaying) 1f else 0.92f,
                        label = ""
                    )

                    FloatingActionButton(
                        onClick = {
                            if (playbackState.isPlaying)
                                playerViewModel.pause() else playerViewModel.resume()
                        },
                        containerColor = colorScheme.primary,
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying)
                                Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = { playerViewModel.skipNext() }) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = colorScheme.onBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = { playerViewModel.setRepeatMode() }) {
                        Icon(
                            imageVector = when (repeatMode) {
                                Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != Player.REPEAT_MODE_OFF)
                                colorScheme.primary else colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatDuration(durationMs: Long): String {
        if (durationMs < 0) return "00:00"
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
