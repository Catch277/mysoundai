package com.example.mysoundai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mysoundai.ui.viewmodel.AuthState
import com.example.mysoundai.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val uiState = authViewModel.authUIState

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthState.Success -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
                onBack()
            }

            is AuthState.Error -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }

            else -> {}
        }
    }
    Column(modifier = Modifier.fillMaxSize().
    padding(24.dp),
        verticalArrangement = Arrangement.Center) {
        Text(text = "Tạo tài khoản mới",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { authViewModel.signUp(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is AuthState.Loading
        ) {
            if (uiState is AuthState.Loading)
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            else Text("Đăng ký")
        }
    }
}