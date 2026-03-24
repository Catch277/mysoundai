package com.example.mysoundai.domain.model


data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val audioUrl: String,
)