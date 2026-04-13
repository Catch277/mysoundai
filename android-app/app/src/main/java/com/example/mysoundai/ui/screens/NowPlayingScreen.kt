package com.example.mysoundai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mysoundai.ui.viewmodel.PlayerViewModel


@Composable
fun NowPlayingScreen(
    playerViewModel: PlayerViewModel,
    onClose: () -> Unit
) {
    val currentSong by playerViewModel.currentSong.collectAsState()
    val playbackState by playerViewModel.playbackState.collectAsState()

    val repeatMode = playbackState.repeatMode
    val isShuffled = playbackState.isShuffleEnabled

    var rotation by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(playbackState.isPlaying) {
        var lastFrame = 0L
        while (playbackState.isPlaying) {
            withFrameMillis { frameMs ->
                if (lastFrame > 0L) rotation += (frameMs - lastFrame) * 0.024F
                lastFrame = frameMs
                if (rotation >= 360f) rotation -= 360f
            }
        }
    }

    if (currentSong == null) return

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
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(currentSong?.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Cover",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .graphicsLayer { rotationZ = rotation },
            placeholder = ColorPainter(Color.DarkGray),
            error = ColorPainter(Color.DarkGray)
        )
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = currentSong?.title ?: "",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentSong?.artist ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(24.dp))

        var sliderPosition by remember { mutableFloatStateOf(0f) }
        var isDragging by remember { mutableStateOf(false) }

        LaunchedEffect(playbackState.currentPosition) {
            if (!isDragging && playbackState.duration > 0) {
                sliderPosition = playbackState.currentPosition.toFloat() / playbackState.duration
            }
        }

        Slider(
            value = sliderPosition,
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
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.DarkGray
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(playbackState.currentPosition),
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
            Text(
                text = formatDuration(playbackState.duration),
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { playerViewModel.setShuffleMode(!isShuffled) }) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffled) Color(0XFF1DB954) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { playerViewModel.skipPrevious() }) {
                Icon(Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp))
            }

            FloatingActionButton(
                onClick = { if (playbackState.isPlaying)
                    playerViewModel.pause() else playerViewModel.resume() },
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = if (playbackState.isPlaying)
                    Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = { playerViewModel.skipNext() }) {
                Icon(Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp))
            }

            IconButton(onClick = { playerViewModel.setRepeatMode() }) {
                Icon(
                    imageVector = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF)
                        Color(0xFF1DB954) else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
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