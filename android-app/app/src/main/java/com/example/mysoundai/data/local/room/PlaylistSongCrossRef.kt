package com.example.mysoundai.data.local.room

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_song_cross_ref",
    primaryKeys = ["playlistId", "songId"],
    indices = [Index(value = ["songId"])]
)
data class PlaylistSongCrossRef(
    val playlistId: Int,
    val songId: String
)