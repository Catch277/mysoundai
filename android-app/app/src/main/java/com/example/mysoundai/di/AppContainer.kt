package com.example.mysoundai.di

import android.content.Context
import com.example.mysoundai.data.local.LanguagePreference
import com.example.mysoundai.data.local.ThemePreference
import com.example.mysoundai.data.repository.AuthRepository
import com.example.mysoundai.data.repository.MusicRepositoryImpl
import com.example.mysoundai.domain.repository.MusicRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit
import androidx.room.Room
import com.example.mysoundai.data.local.room.AppDatabase
import com.example.mysoundai.data.repository.DownloadRepository
import com.example.mysoundai.data.repository.FavoriteRepository
import com.example.mysoundai.data.repository.PlaylistRepository
import com.example.mysoundai.service.MusicController
import okhttp3.OkHttpClient
import kotlin.getValue

object AppContainer {
    lateinit var themePreference: ThemePreference
        private set
    lateinit var languagePreference: LanguagePreference
        private set
    private var appContext: Context? = null
    private val ctx get() = appContext
        ?: error("AppContainer.init(context) must be called before accessing any property")
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        themePreference = ThemePreference(context)
        languagePreference = LanguagePreference(context)
    }

    val musicRepository: MusicRepository by lazy {
        MusicRepositoryImpl(NetworkModule.apiService)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(firebaseAuth, ctx)
    }

    val musicController: MusicController by lazy {
        MusicController(ctx)
    }

    val downloadRepository: DownloadRepository by lazy {
        DownloadRepository(songDao = database.songDao(),
            context = ctx,
            okHttpClient = okHttpClient)
    }

    val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepository(favoriteDao = database.favoriteDao())
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(playlistDao = database.playlistDao())
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun saveLanguageSync(langCode: String) {
        val prefs = ctx.getSharedPreferences("lang_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("language_code", langCode) }
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "mysoundai_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }
}