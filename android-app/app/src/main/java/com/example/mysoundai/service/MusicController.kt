package com.example.mysoundai.service

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.SessionToken
import com.example.mysoundai.domain.model.Song
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val currentMediaId: String = "",
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val isShuffleEnabled: Boolean = false
)
class MusicController(private val context: Context) {
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val _mediaControllerFlow = MutableStateFlow<MediaController?>(null)

    private var pendingSong: Pair<Song, String>? = null


    init {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicPlaybackService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.let { future ->
            future.addListener({
                try {
                    mediaController = future.get()
                    _mediaControllerFlow.value = mediaController
                    pendingSong?.let { (song, uri) ->
                        playSong(song, uri)
                        pendingSong = null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    fun playSong(song: Song, fileUri: String) {
        val controller = mediaController ?: run {
            pendingSong = Pair(song, fileUri)
            return
        }

        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(fileUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setArtworkUri(song.imageUrl?.toUri())
                    .build()
            )
            .build()

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    fun playPlayList(songs: List<Song>, startIndex: Int, localPaths: Map<String, String>) {
        val controller = mediaController ?: return

        val mediaItems = songs.map { song ->
            val uriToPlay = localPaths[song.id] ?: song.audioUrl ?: ""
            MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(uriToPlay)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setArtworkUri(song.imageUrl?.toUri())
                        .build()
                )
                .build()
        }
                controller.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
                controller.prepare()
                controller.play()
        }

    fun skipToNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun skipToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun resume() {
        mediaController?.play()
    }

    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun toggleShuffle(enabled: Boolean) {
        mediaController?.shuffleModeEnabled = enabled
    }

    fun toggleRepeatMode() {
        val controller = mediaController ?: return
        val newMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        controller.repeatMode = newMode
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPlaybackState(): Flow<PlaybackState> = _mediaControllerFlow
        .filterNotNull()
        .flatMapLatest { controller ->
            callbackFlow {
                fun updateState() {
                    trySend(
                        PlaybackState(
                            isPlaying = controller.isPlaying,
                            currentPosition = controller.currentPosition,
                            duration = controller.duration.coerceAtLeast(0L),
                            currentMediaId = controller.currentMediaItem?.mediaId ?: "",
                            repeatMode = controller.repeatMode,
                            isShuffleEnabled = controller.shuffleModeEnabled
                        )
                    )
                }
                val listener = object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) = updateState()

                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) = updateState()

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = updateState()

                    override fun onRepeatModeChanged(repeatMode: Int) = updateState()

                    override fun onShuffleModeEnabledChanged(shuffledModeEnabled: Boolean) = updateState()
                }
                controller.addListener(listener)
                updateState()

                val progressJob = launch {
                    while (isActive) {
                        if (controller.isPlaying) updateState()
                        delay(1000L)
                    }
                }
                awaitClose {
                    controller.removeListener(listener)
                    progressJob.cancel()
                }
            }
        }
}

