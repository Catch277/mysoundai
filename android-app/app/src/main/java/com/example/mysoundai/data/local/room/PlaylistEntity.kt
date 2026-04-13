package com.example.mysoundai.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)