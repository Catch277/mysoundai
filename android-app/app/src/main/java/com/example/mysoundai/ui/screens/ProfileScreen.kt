package com.example.mysoundai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import com.example.mysoundai.data.model.UserData


@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onSettingsClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.
        fillMaxSize().
        padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cá nhân",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,

            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",

                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // --- AUTH SECTION  ---
        if (user != null) {
            UserInfoSection(user = user!!) { authViewModel.handleSignOut() }
        } else {
                LoginOptionsSection(
                    onLoginEmailClick = onLoginClick,
                    onGoogleLoginClick = { /* Handle Google login */ },
                    onFacebookLoginClick = { /* Handle Facebook login */ }
                )
            }
        }
   }
@Composable
fun LoginOptionsSection(
    onLoginEmailClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onFacebookLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Đăng nhập để lưu ngay danh sách phát của bạn",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,

        )
        Button(
            onClick = onLoginEmailClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
        ) {
            Text(text = "Đăng nhập / Đăng ký với Email")
        }
        Text(text = "hoặc", style = MaterialTheme.typography.bodySmall)
        OutlinedButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Tiếp tục với Google")
        }
        OutlinedButton(
            onClick = onFacebookLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tiếp tục với Facebook")
        }
    }
}

@Composable
fun UserInfoSection(user: UserData,
                    onSignOutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user.profilePictureUrl != null)
        {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "Ảnh đại diện",
                modifier = Modifier.size(100.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Ảnh đại diện",
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.userName ?: "Người dùng MySoundAI",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onSignOutClick) {
            Text("Đăng xuất", color = Color.Red)
        }
    }
}

