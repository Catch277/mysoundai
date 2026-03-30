package com.example.mysoundai.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBack: () -> Unit,
    onNavigateToUpdateProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                ListItem(
                    headlineContent = { Text("Cập nhật hồ sơ") },
                    supportingContent = { Text("Thay đổi tên hiển thị, ảnh đại diện") },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onNavigateToUpdateProfile() }
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Giao diện") },
                    supportingContent = { Text("Thiết lập giao diện Sáng, Tối, Hệ thống") },
                    leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                    modifier = Modifier.alpha(0.5f)
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Ngôn ngữ") },
                    supportingContent = { Text("Tiếng Việt") },
                    leadingContent = { Icon(Icons.Default.Flag, contentDescription = null) },
                    modifier = Modifier.alpha(0.5f)
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Đã tải") },
                    supportingContent = { Text("Quản lý nhạc ngoại tuyến") },
                    leadingContent = { Icon(Icons.Default.Download, null) },
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }
}