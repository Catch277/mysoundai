package com.example.mysoundai.ui.screens

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.mysoundai.ui.components.ToastMessage
import com.example.mysoundai.ui.viewmodel.AuthState
import kotlinx.coroutines.delay

@Composable
fun UpdateProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var newDisplayname by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var toastMessage by remember { mutableStateOf("") }
    val uiState = authViewModel.authUIState
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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

            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cập nhật hồ sơ",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Back"
                    )
                }
            }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(24.dp))
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Ảnh đại diện mới",
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                ) {
                    Text("Chọn ảnh từ thư viện ảnh")
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newDisplayname,
                    onValueChange = { newDisplayname = it },
                    label = { Text("Tên người dùng mới") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        authViewModel.updateProfile(
                            displayName = newDisplayname.ifBlank { null },
                            photoUri = selectedImageUri
                        )
                    },
                    colors = ButtonDefaults.buttonColors(   Color(0xFF1DB954)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is AuthState.Loading
                ) {
                    if (uiState is AuthState.Loading)
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    else Text("Lưu cập nhật",
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
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
