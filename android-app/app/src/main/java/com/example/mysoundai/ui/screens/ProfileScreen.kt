package com.example.mysoundai.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import com.example.mysoundai.R
import com.example.mysoundai.ui.viewmodel.AuthState


@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onSettingsClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsStateWithLifecycle()
    val uiState = authViewModel.authUIState


    Column(
        modifier = Modifier.
        fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                    text = stringResource(R.string.profile_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                    )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.action_settings),
                        tint = Color.White
                        )
                }
        }


        // --- AUTH SECTION  ---
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(32.dp))
            user?.let {
                 UserInfoSection(user = it) { authViewModel.handleSignOut() }
            }   ?:    LoginOptionsSection(
                    onLoginEmailClick = onLoginClick,
                    onGoogleLoginClick = { /* Handle Google login */ },
                    onFacebookLoginClick = { /* Handle Facebook login */ },
                    isLoading = uiState is AuthState.Loading
                )
            }
        }
   }
@Composable
fun LoginOptionsSection(
    onLoginEmailClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onFacebookLoginClick: () -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_login_prompt),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,

        )
        Button(
            onClick = onLoginEmailClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
        ) {
            Text(text = stringResource(R.string.profile_login_email))
        }
        Text(text = stringResource(R.string.profile_or), style = MaterialTheme.typography.bodySmall)
        OutlinedButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(text = stringResource(R.string.profile_login_google))
        }
        OutlinedButton(
            onClick = onFacebookLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(stringResource(R.string.profile_login_facebook))
        }
    }
}

@Composable
fun UserInfoSection(user: UserData, onSignOutClick: () -> Unit) {
    val avatarDesc = stringResource(R.string.profile_avatar_description)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user.profilePictureUrl != null)
        {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = avatarDesc,
                modifier = Modifier.size(100.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = avatarDesc,
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = user.userName ?: stringResource(R.string.profile_default_username),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSignOutClick,
            colors = ButtonDefaults.buttonColors(Color(0xFF1DB954))) {
            Text(stringResource(R.string.profile_sign_out),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}

