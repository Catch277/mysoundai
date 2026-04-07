package com.example.mysoundai.domain.model


data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val imageUrl: String,
    val audioUrl: String,
)