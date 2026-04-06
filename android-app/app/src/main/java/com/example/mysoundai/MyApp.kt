package com.example.mysoundai

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.mysoundai.di.AppContainer

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
        restoreLanguage()
    }

    private fun restoreLanguage() {
        val prefs = getSharedPreferences("lang_prefs",Context.MODE_PRIVATE)
        val savedLang = prefs.getString("language_code", null)
        if (savedLang != null) {
            val locales = LocaleListCompat.forLanguageTags(savedLang)
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }
}