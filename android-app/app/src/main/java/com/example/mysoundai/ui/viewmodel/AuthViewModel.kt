package com.example.mysoundai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoundai.data.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val currentUser = repository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getCurrentUser()
    )

    fun handleSignOut() {
        repository.signOut()
    }
}