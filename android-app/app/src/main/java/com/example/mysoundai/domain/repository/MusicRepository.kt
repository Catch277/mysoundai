package com.example.mysoundai.domain.repository

import com.example.mysoundai.domain.model.Song

interface MusicRepository {
    suspend fun getTrendingSongs(): List<Song>
}