package com.example.mysoundai.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.langDataStore by preferencesDataStore("language_settings")

class LanguagePreference(context: Context) {
    private val appContext = context.applicationContext
    private val langKey = stringPreferencesKey("language_code")

    val languageFlow: Flow<String?> = appContext.langDataStore.data.map { prefs ->
        prefs[langKey]
    }

    suspend fun saveLanguage(langCode: String) {
        appContext.langDataStore.edit { prefs ->
            prefs[langKey] = langCode
        }
    }
}