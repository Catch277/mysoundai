package com.example.mysoundai.di

import com.example.mysoundai.data.repository.MusicRepositoryImpl
import com.example.mysoundai.domain.repository.MusicRepository

object AppContainer {
    val musicRepository: MusicRepository by lazy {
        MusicRepositoryImpl(NetworkModule.apiService)
    }
}