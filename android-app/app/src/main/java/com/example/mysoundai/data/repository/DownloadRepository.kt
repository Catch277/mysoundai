package com.example.mysoundai.data.repository

import android.content.Context
import android.os.Environment
import com.example.mysoundai.data.local.room.DownloadedSong
import com.example.mysoundai.data.local.room.SongDao
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

class DownloadRepository(
    private val songDao: SongDao,
    private val context: Context
) {
    val allDownloadedSongs: Flow<List<DownloadedSong>> = songDao.getAllDownloadedSongs()

    // Bọc trong Dispatchers.IO để chạy nền
     suspend fun downloadSong(song: Song): Flow<DownloadState> = flow {
        emit(DownloadState.Downloading(0))
        for (i in 1..100 step 10) {
            delay(200)
            emit(DownloadState.Downloading(i))
        }
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "${song.id}.mp3")
        file.writeText("fake mp3 content")
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