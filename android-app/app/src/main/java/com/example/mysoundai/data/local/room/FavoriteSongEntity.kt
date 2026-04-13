package com.example.mysoundai.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysoundai.domain.model.Song

@Entity("favorite_songs")
data class FavoriteSongEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val imageUrl: String?,
    val audioUrl: String?
) {
    fun toSong() = Song(
        id = songId,
        title = title,
        artist = artist,
        duration = duration,
        imageUrl = imageUrl,
        audioUrl = audioUrl
    )
}

fun Song.toFavoriteSongEntity() = FavoriteSongEntity(
    songId = id,
    title = title,
    artist = artist,
    duration = duration,
    imageUrl = imageUrl,
    audioUrl = audioUrl
)


