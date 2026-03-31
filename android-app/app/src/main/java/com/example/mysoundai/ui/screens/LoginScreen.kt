package com.example.mysoundai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mysoundai.ui.components.ToastMessage
import com.example.mysoundai.ui.viewmodel.AuthState
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val uiState = authViewModel.authUIState

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthState.Success -> {
                toastMessage = uiState.message
                authViewModel.resetState()
                onBack()
            }

            is AuthState.Error -> {
                toastMessage = uiState.message
                authViewModel.resetState()
            }

            else -> {
            }
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Đăng nhập",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    isError = email.isEmpty() && toastMessage.contains("email"),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = password.isEmpty() && toastMessage.contains("mật khẩu"),
                    visualTransformation = if (isPasswordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPasswordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Visibility")
                        }
                    }
                )
                TextButton(
                    onClick = {
                        email = email.trim()
                        if (email.isNotEmpty()) {
                            authViewModel.sendPasswordResetEmail(email)
                            toastMessage = "Đã gửi link reset mật khẩu đến email của bạn!"
                        } else {
                            toastMessage = "Vui lòng nhập email của bạn!"
                        }
                    },
                    modifier = Modifier.wrapContentWidth(Alignment.End)
                ) {
                    Text("Quên mật khẩu?", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        email = email.trim()
                        password = password.trim()
                        when {
                            email.isEmpty() && password.isEmpty() -> {
                                toastMessage = "Vui lòng nhập email và mật khẩu!"
                            }
                            email.isEmpty() -> {
                                toastMessage = "Vui lòng nhập email!"
                            }
                            password.isEmpty() -> {
                                toastMessage = "Vui lòng nhập mật khẩu!"
                            }
                            password.length < 6 -> {
                                toastMessage = "Mật khẩu phải có ít nhất 6 ký tự!"
                            }
                            else -> {
                                authViewModel.signInWithEmail(email, password)
                            }
                        }
                        },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    enabled = uiState !is AuthState.Loading
                ) {
                    if (uiState is AuthState.Loading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    else Text("Đăng nhập")
                }
                TextButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chưa có tài khoản? Đăng ký ngay")
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