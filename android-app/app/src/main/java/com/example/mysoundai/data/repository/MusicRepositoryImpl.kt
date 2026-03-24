package com.example.mysoundai.data.repository

import com.example.mysoundai.data.remote.MusicAPIService
import com.example.mysoundai.domain.model.Song
import com.example.mysoundai.domain.repository.MusicRepository

class MusicRepositoryImpl(
    private val apiService: MusicAPIService
) : MusicRepository {
    override suspend fun getTrendingSongs(): List<Song> {
        return apiService.getRecomendations()
    }
}