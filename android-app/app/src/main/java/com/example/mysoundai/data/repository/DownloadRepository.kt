package com.example.mysoundai.data.repository

import android.content.Context
import android.os.Environment
import com.example.mysoundai.R
import com.example.mysoundai.data.local.room.DownloadedSong
import com.example.mysoundai.data.local.room.SongDao
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

class DownloadRepository(
    private val songDao: SongDao,
    private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    val allDownloadedSongs: Flow<List<DownloadedSong>> = songDao.getAllDownloadedSongs()

    // Bọc trong Dispatchers.IO để chạy nền
     suspend fun downloadSong(song: Song): Flow<DownloadState> = flow {
        emit(DownloadState.Downloading(0))
        val audioUrl = song.audioUrl
        if (audioUrl.isNullOrEmpty()) {
            emit(DownloadState.Failed(context.getString(R.string.error_invalid_audio_url)))
            return@flow
        }

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "${song.id}.mp3")
        try {
            val request = Request.Builder().url(audioUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                emit(DownloadState.Failed(context.getString(R.string.error_server_code, response.code)))
                return@flow
            }

            val body = response.body ?: run {
                emit(DownloadState.Failed(context.getString(R.string.error_no_music_data)))
                return@flow
            }

            val contentLength = body.contentLength()
            var bytesCopied: Long = 0
            val buffer = ByteArray(8 * 1024)
            var lastProgess = 0

            body.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        currentCoroutineContext().ensureActive()
                        output.write(buffer, 0, bytes)
                        bytesCopied += bytes

                        if (contentLength > 0) {
                            val progress = ((bytesCopied * 100) / contentLength).toInt()
                            if (progress > lastProgess) {
                                emit(DownloadState.Downloading(progress))
                                lastProgess = progress
                            }
                        }
                        bytes = input.read(buffer)
                    }
                    output.flush()
                }
            }
            emit(DownloadState.Downloading(100))
            songDao.insertSong(
                DownloadedSong(
                    songId = song.id,
                    title = song.title,
                    artist = song.artist,
                    duration = song.duration,
                    filePath = file.absolutePath,
                    coverPath = song.imageUrl,
                    downloadDate = System.currentTimeMillis()
                )
            )
            emit(DownloadState.Completed)
        } catch (e: CancellationException) {
            if (file.exists()) file.delete()
            throw e
        } catch (e: Exception) {
            if (file.exists()) file.delete()
            emit(DownloadState.Failed(
                e.localizedMessage ?: context.getString(R.string.error_network_or_system)))
        }
    }.flowOn(Dispatchers.IO)



    suspend fun deleteSong(song: DownloadedSong) {
        withContext(Dispatchers.IO) {
            // 1. Xóa file vật lý để giải phóng bộ nhớ
            val file = File(song.filePath)
            if (file.exists()) {
                file.delete()
            }
            // 2. Xóa data trong Room
            songDao.deleteSongById(song.songId)
        }
    }

    suspend fun isDownloaded(songId: String): Boolean {
        return songDao.isSongDownloaded(songId)
    }
}