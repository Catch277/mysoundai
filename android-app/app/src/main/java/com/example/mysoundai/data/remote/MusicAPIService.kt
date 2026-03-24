package com.example.mysoundai.data.remote

import com.example.mysoundai.domain.model.Song
import retrofit2.http.GET


interface MusicAPIService {
    @GET("recommendations")
    suspend fun getRecomendations(): List<Song>
}


