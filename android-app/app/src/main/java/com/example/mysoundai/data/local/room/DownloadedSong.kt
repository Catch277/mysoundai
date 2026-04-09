package com.example.mysoundai.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysoundai.domain.model.Song

@Entity(tableName = "downloaded_songs")
data class DownloadedSong(
    @PrimaryKey
    val songId: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val filePath: String,
    val coverPath: String?,
    val downloadDate: Long
)

fun DownloadedSong.toDomain(): Song {
    return Song(
        id = songId,
        title = title,
        artist = artist,
        duration = duration,
        imageUrl = coverPath ?: "",
        audioUrl = filePath
    )
}