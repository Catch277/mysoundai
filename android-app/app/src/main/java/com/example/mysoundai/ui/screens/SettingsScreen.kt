package com.example.mysoundai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mysoundai.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onNavigateToUpdateProfile: () -> Unit,
    onBack: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsStateWithLifecycle(null)
    val isLoggedIn = user != null
    var showDialog by remember { mutableStateOf(false) }
    val currentTheme = authViewModel.userSettings["theme"] as? String ?: "SYSTEM"
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cài đặt",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isLoggedIn) {
                    Text("Vui lòng đăng nhập để tùy chỉnh",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Back"
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Cập nhật Hồ sơ
            item {
                ListItem(
                    headlineContent = { Text("Hồ sơ",
                                      color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    supportingContent = { Text("Thay đổi tên hiển thị, ảnh đại diện",
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null,
                        tint = if (isLoggedIn) LocalContentColor.current else Color.Gray) },
                    trailingContent = { if (isLoggedIn) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    }
                    },
                    modifier = Modifier.clickable(enabled = isLoggedIn) {
                        onNavigateToUpdateProfile()
                    }
                )
                HorizontalDivider()
            }
            // Cập nhật Giao diện
            item {
                val themeLabel = when(currentTheme) {
                    "LIGHT" -> "Sáng"
                    "DARK" -> "Tối"
                    else -> "Hệ thống"
                }
                ListItem(
                    headlineContent = { Text("Giao diện",
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    supportingContent = { Text("Thiết lập giao diện chủ đề\nChế độ hiện tại: $themeLabel",
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Palette, contentDescription = null,
                        tint = if (isLoggedIn) LocalContentColor.current else Color.Gray) },
                    trailingContent = { if (isLoggedIn) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    }
                                      },
                    modifier = Modifier.clickable(enabled = isLoggedIn) {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Chọn chế độ giao diện") },
                        text = {
                            Column {
                                listOf("LIGHT" to "Sáng", "DARK" to "Tối", "SYSTEM" to "Hệ thống")
                                    .forEach { (mode, label) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    authViewModel.updateSetting("theme", mode)
                                                    showDialog = false
                                                }
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = currentTheme == mode,
                                                onClick = null
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(label)
                                        }
                                    }
                            }
                        },
                        confirmButton = {}
                    )
                }
                HorizontalDivider()
            }
            // Cập nhật Ngôn ngữ
            item {
                ListItem(
                    headlineContent = { Text("Ngôn ngữ") },
                    supportingContent = { Text("Tiếng Việt") },
                    leadingContent = { Icon(Icons.Default.Flag, contentDescription = null) },
                    modifier = Modifier.alpha(0.5f)
                )
                HorizontalDivider()
            }
            // Cập nhật Đã tải
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