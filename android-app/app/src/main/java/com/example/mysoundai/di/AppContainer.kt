package com.example.mysoundai.di

import com.example.mysoundai.data.repository.AuthRepository
import com.example.mysoundai.data.repository.MusicRepositoryImpl
import com.example.mysoundai.domain.repository.MusicRepository
import com.google.firebase.auth.FirebaseAuth

object AppContainer {
    val musicRepository: MusicRepository by lazy {
        MusicRepositoryImpl(NetworkModule.apiService)
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(firebaseAuth)
    }
}