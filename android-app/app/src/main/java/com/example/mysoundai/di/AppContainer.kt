package com.example.mysoundai.di

import android.content.Context
import com.example.mysoundai.data.local.LanguagePreference
import com.example.mysoundai.data.local.ThemePreference
import com.example.mysoundai.data.repository.AuthRepository
import com.example.mysoundai.data.repository.MusicRepositoryImpl
import com.example.mysoundai.domain.repository.MusicRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import com.example.mysoundai.data.local.room.AppDatabase
import com.example.mysoundai.data.repository.DownloadRepository
import com.example.mysoundai.ui.viewmodel.DownloadViewModel

object AppContainer {
    lateinit var themePreference: ThemePreference
        private set
    lateinit var languagePreference: LanguagePreference
        private set
    private lateinit var appContext: Context

    fun init(context: Context) {
        themePreference = ThemePreference(context)
        languagePreference = LanguagePreference(context)
        appContext = context.applicationContext
    }

    val musicRepository: MusicRepository by lazy {
        MusicRepositoryImpl(NetworkModule.apiService)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(firebaseAuth)
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun saveLanguageSync(langCode: String) {
        val prefs = appContext.getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("language_code", langCode) }
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "mysoundai_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    val downloadRepository: DownloadRepository by lazy {
        DownloadRepository(database.songDao(), appContext)
    }

    val Factory = viewModelFactory {
        initializer {
            DownloadViewModel(repository = downloadRepository)
        }
    }
}