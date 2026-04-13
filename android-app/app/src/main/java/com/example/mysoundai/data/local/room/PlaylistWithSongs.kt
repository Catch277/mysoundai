package com.example.mysoundai.data.local.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithSongs(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",            // ID của PlaylistEntity
        entityColumn = "songId",        // ID của FavoriteSongEntity
        associateBy = Junction(
            value = PlaylistSongCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "songId"
        )
    )
    val songs: List<FavoriteSongEntity>
)