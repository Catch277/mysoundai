package com.example.mysoundai.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.data.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    data class Success(val message: String): AuthState()
    data class Error(val message: String): AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    var authUIState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authUIState = AuthState.Loading
            val result = repository.signUpWithEmail(email, password)
            authUIState = if (result.isSuccess) {
                AuthState.Success("Đăng ký thành công")
            } else AuthState.Error(result.exceptionOrNull()?.message ?: "Đăng ký thất bại")
        }
    }
    fun resetState() {
        authUIState = AuthState.Idle
    }
    val currentUser = repository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getCurrentUser()
    )

    fun handleSignOut() {
        repository.signOut()
        authUIState = AuthState.Idle
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
        authUIState = AuthState.Loading
            val result = repository.signInWithEmail(email, password)
        authUIState = if (result.isSuccess)
            AuthState.Success("Đăng nhập thành công")
         else AuthState.Error(result.exceptionOrNull()?.message ?: "Đăng nhập thất bại")
        }
    }

    fun sendPasswordResetEmail(email: String) {
        authUIState = AuthState.Idle
        viewModelScope.launch {
            val result = repository.sendPasswordResetEmail(email)
            authUIState = if (result.isSuccess)
                AuthState.Success("Đã gửi link reset mật khẩu đến email của bạn")
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Gửi link thất bại")
        }
    }

    fun updateProfile(displayName: String? = null, photoUri: Uri? = null) {
        viewModelScope.launch {
            val result = repository.updateProfile(displayName, photoUri)
            authUIState = if (result.isSuccess)
                AuthState.Success("Cập nhật thành công")
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Cập nhật thất bại")
        }
    }
}