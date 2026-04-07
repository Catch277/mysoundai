package com.example.mysoundai.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

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