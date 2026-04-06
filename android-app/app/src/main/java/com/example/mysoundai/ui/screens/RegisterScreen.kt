package com.example.mysoundai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mysoundai.R
import com.example.mysoundai.ui.components.ToastMessage
import com.example.mysoundai.ui.viewmodel.AuthState
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    val uiState = authViewModel.authUIState

    val msgEmailAndPassword = stringResource(R.string.login_error_email_and_password)
    val msgEmailEmpty = stringResource(R.string.login_error_email_empty)
    val msgPasswordEmpty = stringResource(R.string.login_error_password_empty)
    val msgPasswordShort = stringResource(R.string.login_error_password_short)
    val msgSignInSuccess = stringResource(R.string.login_success)
    val msgSignInFailed = stringResource(R.string.login_error_failed)
    val msgResetSent = stringResource(R.string.login_reset_sent)
    val msgResetFailed = stringResource(R.string.login_error_reset_failed)

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthState.Success -> {
                toastMessage = when (uiState.message) {
                    "SIGN_IN_SUCCESS" -> msgSignInSuccess
                    "RESET_EMAIL_SENT" -> msgResetSent
                    else -> uiState.message
                }
                authViewModel.resetState()
                onBack()
            }
            is AuthState.Error -> {
                toastMessage = when (uiState.message) {
                    "ERROR_SIGN_IN_FAILED" -> msgSignInFailed
                    "ERROR_RESET_FAILED" -> msgResetFailed
                    else -> uiState.message
                }
                authViewModel.resetState()
            }
            else -> {}
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                    Text(
                        stringResource(R.string.register_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(all = 24.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.login_password_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = stringResource(R.string.login_toggle_password)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        email = email.trim()
                        when {
                            email.isEmpty() && password.isEmpty() -> toastMessage =
                                msgEmailAndPassword

                            email.isEmpty() -> toastMessage = msgEmailEmpty
                            password.isEmpty() -> toastMessage = msgPasswordEmpty
                            password.length < 6 -> toastMessage = msgPasswordShort
                            else -> authViewModel.signUp(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    enabled = uiState !is AuthState.Loading
                ) {
                    if (uiState is AuthState.Loading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    else Text(stringResource(R.string.register_button))
                }
            }
        }
        if (toastMessage.isNotEmpty()) {
            ToastMessage(message = toastMessage, duration = 2000)
            LaunchedEffect(toastMessage) {
                delay(2000)
                toastMessage = ""
            }
        }
    }
}