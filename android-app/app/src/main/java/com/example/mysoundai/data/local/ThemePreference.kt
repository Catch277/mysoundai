package com.example.mysoundai.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class ThemePreference(context: Context) {
    private val appContext = context.applicationContext
    private val themeKey = stringPreferencesKey("theme_mode")

    val themeFlow: Flow<String?> = appContext.dataStore.data.map { preferences ->
        preferences[themeKey] ?: "SYSTEM"
    }

    suspend fun saveTheme(mode: String) {
        appContext.dataStore.edit { preferences ->
            preferences[themeKey] = mode
        }
    }
}