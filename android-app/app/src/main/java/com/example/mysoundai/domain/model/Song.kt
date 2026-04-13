package com.example.mysoundai.domain.model

import com.google.gson.annotations.SerializedName


data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val imageUrl: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
)