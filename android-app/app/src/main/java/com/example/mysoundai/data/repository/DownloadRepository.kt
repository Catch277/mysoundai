package com.example.mysoundai.data.repository

import android.content.Context
import com.example.mysoundai.data.local.room.DownloadedSong
import com.example.mysoundai.data.local.room.SongDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class DownloadRepository(
    private val songDao: SongDao,
    private val context: Context
) {
    val allDownloadedSongs: Flow<List<DownloadedSong>> = songDao.getAllDownloadedSongs()

    // Bọc trong Dispatchers.IO để chạy nền
    suspend fun saveDownloadedSong(
        songId: String,
        title: String,
        artist: String,
        duration: Long,
        // Sau này bạn truyền InputStream hoặc ByteArray của file MP3 vào đây để lưu
    ) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Giả lập tạo đường dẫn file trong Internal Storage
                // (Khi kết nối API thật, bạn sẽ dùng FileOutputStream để ghi byte vào đường dẫn này)
                val audioFileName = "$songId.mp3"
                val localAudioPath = File(context.filesDir, audioFileName).absolutePath

                // 2. Lưu thông tin (Metadata) vào Room Database
                val newSong = DownloadedSong(
                    songId = songId,
                    title = title,
                    artist = artist,
                    duration = duration,
                    filePath = localAudioPath,
                    coverPath = null, // Tạm thời để null, xử lý ảnh bìa sau
                    downloadDate = System.currentTimeMillis()
                )

                songDao.insertSong(newSong)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
}