package com.example.mysoundai.di

import com.example.mysoundai.data.remote.MusicAPIService
import com.example.mysoundai.domain.model.Song
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkModule {
    private const val BASE_URL = "http://10.0.2.2:8000"

    val apiService: MusicAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicAPIService::class.java)
    }
}