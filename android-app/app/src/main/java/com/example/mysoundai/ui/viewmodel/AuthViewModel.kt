package com.example.mysoundai.ui.viewmodel

import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.data.local.LanguagePreference
import com.example.mysoundai.data.local.ThemePreference
import com.example.mysoundai.data.model.UserData
import com.example.mysoundai.data.repository.AuthRepository
import com.example.mysoundai.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    data class Success(val message: String): AuthState()
    data class Error(val message: String): AuthState()
}

class AuthViewModel(private val repository: AuthRepository,
                    private val themePreference: ThemePreference,
                    private val languagePreference: LanguagePreference) : ViewModel() {
    var authUIState by mutableStateOf<AuthState>(AuthState.Idle)
        private set
    var userSettings by mutableStateOf<Map<String, Any>>(emptyMap())
        private set
    private val _currentUser = MutableStateFlow(repository.getCurrentUser())
    val currentUser : StateFlow<UserData?> = _currentUser.asStateFlow()
    private var previousUser: UserData? = null

    init {
        viewModelScope.launch {
            repository.authState.collect { user ->
                _currentUser.value = user
                if (user != null && previousUser == null)
                    loadSettings()
                if (!userSettings.containsKey("theme")) {
                    val localTheme = themePreference.themeFlow.first() ?: "SYSTEM"
                    userSettings = userSettings.toMutableMap().also { it["theme"] = localTheme }
                }
                previousUser = user
            }
        }
        viewModelScope.launch {
            val savedLang = languagePreference.languageFlow.first()
            if (savedLang != null)
                applyLocale(savedLang)
        }
    }


    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authUIState = AuthState.Loading
            val result = repository.signUpWithEmail(email, password)
            authUIState = if (result.isSuccess) {
                AuthState.Success("SIGN_UP_SUCCESS")
            } else AuthState.Error(result.exceptionOrNull()?.message ?: "ERROR_SIGN_UP_FAILED")
        }
    }
    fun resetState() {
        authUIState = AuthState.Idle
    }

    fun handleSignOut() {
        val currentLang = userSettings["language"] as? String
        userSettings = emptyMap()
        previousUser = null
        _currentUser.value = null
        repository.signOut()
        authUIState = AuthState.Idle
        viewModelScope.launch {
            val savedTheme = themePreference.themeFlow.first() ?: "SYSTEM"
            userSettings = buildMap {
                put("theme", savedTheme)
                currentLang?.let { put("language", it) }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
        authUIState = AuthState.Loading
            val result = repository.signInWithEmail(email, password)
        authUIState = if (result.isSuccess)
            AuthState.Success("SIGN_IN_SUCCESS")
         else AuthState.Error(result.exceptionOrNull()?.message ?: "ERROR_SIGN_IN_FAILED")
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            authUIState = AuthState.Loading
            val result = repository.sendPasswordResetEmail(email)
            authUIState = if (result.isSuccess)
                AuthState.Success("RESET_EMAIL_SENT")
            else AuthState.Error(result.exceptionOrNull()?.message ?: "ERROR_RESET_FAILED")
        }
    }

    // Update user avatar, username, etc
    fun updateProfile(displayName: String? = null, photoUri: Uri? = null) {
        viewModelScope.launch {
            authUIState = AuthState.Loading
            val result = repository.updateProfile(displayName, photoUri)
            if (result.isSuccess) {
                result.getOrNull()?.let { updatedUser ->
                    _currentUser.value = updatedUser
                }
                if (displayName != null) updateSetting("display_name", displayName)
                if (photoUri != null) updateSetting("photo_uri", photoUri.toString())
                authUIState = AuthState.Success("UPDATED_SUCCESS")
            }
            else authUIState = AuthState.Error(result.exceptionOrNull()?.message ?: "ERROR_UPDATE_FAILED")
        }
    }

    // Update user's language mode
    fun updateLanguage(langCode: String) {
        updateSetting("language", langCode)
        viewModelScope.launch {
            languagePreference.saveLanguage(langCode)
        }
        AppContainer.saveLanguageSync(langCode)
        applyLocale(langCode)
    }

    private fun applyLocale(langCode: String) {
        val locales = LocaleListCompat.forLanguageTags(langCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    //load setting when user logged in successfully
    suspend fun loadSettings() {
        val result = repository.getUserSettings()
        if (result.isSuccess) {
            result.getOrNull()?.let { userSettings = it }
        }
    }

    //Update specific setting
    fun updateSetting(key: String, value: Any) {
        val newSettings =  userSettings.toMutableMap()
        newSettings[key] = value
        userSettings = newSettings
        viewModelScope.launch {
            repository.saveUserSettings(mapOf(key to value))
            if (key == "theme")
                themePreference.saveTheme(value.toString())
        }
    }

}