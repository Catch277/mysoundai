package com.example.mysoundai.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mysoundai.R
import com.example.mysoundai.ui.components.ToastMessage
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onNavigateToUpdateProfile: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onBack: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsStateWithLifecycle(null)
    val isLoggedIn = user != null
    var showThemeDialog by remember { mutableStateOf(false) }
    val userSettings = authViewModel.userSettings

    val currentTheme = userSettings["theme"] as? String ?: "SYSTEM"
    var toastMessage by remember { mutableStateOf("") }
    var showLangDialog by remember { mutableStateOf(false) }

    val labelLight      = stringResource(R.string.theme_light)
    val labelDark       = stringResource(R.string.theme_dark)
    val labelSystem     = stringResource(R.string.theme_system)
    val msgThemeChanged = stringResource(R.string.settings_theme_changed)

    val langVietnamese  = stringResource(R.string.lang_vietnamese)
    val langEnglish     = stringResource(R.string.lang_english)
    val msgLangChanged  = stringResource(R.string.settings_lang_changed)
    val currentLang = authViewModel.userSettings["language"] as? String
        ?: AppCompatDelegate.getApplicationLocales()
            .toLanguageTags().take(2).ifEmpty { "en" }
    val currentLangLabel = if (currentLang == "en") langEnglish else langVietnamese

    val themeLabel = when (currentTheme) {
        "LIGHT" -> labelLight
        "DARK" -> labelDark
        else -> labelSystem
    }
    val themeOptions = listOf(
        "LIGHT" to labelLight,
        "DARK" to labelDark,
        "SYSTEM" to labelSystem
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                    Text(
                        stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                    if (!isLoggedIn) {
                        Text(
                            stringResource(R.string.settings_login_hint),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
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
                    headlineContent = { Text(stringResource(R.string.settings_profile),
                                      color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    supportingContent = { Text(stringResource(R.string.settings_profile_desc),
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null,
                        tint = if (isLoggedIn) LocalContentColor.current else Color.Gray) },
                    trailingContent = { if (isLoggedIn) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        ) }
                    },
                    modifier = Modifier
                        .alpha(if (isLoggedIn) 1f else 0.5f)
                        .clickable(enabled = isLoggedIn) {
                        onNavigateToUpdateProfile()
                    }
                )
                HorizontalDivider()
            }
            // Cập nhật Giao diện
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_theme),
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    supportingContent = { Text(stringResource(R.string.settings_theme_desc, themeLabel),
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
                    modifier = Modifier
                        .alpha(if (isLoggedIn) 1f else 0.5f)
                        .clickable(enabled = isLoggedIn) {
                        showThemeDialog = true
                    }
                )
                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text(stringResource(R.string.settings_theme_dialog_title)) },
                        text = {
                            Column {
                                themeOptions.forEach { (mode, label) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    authViewModel.updateSetting("theme", mode)
                                                    toastMessage = "$msgThemeChanged $label"
                                                    showThemeDialog = false
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
                    headlineContent = { Text(stringResource(R.string.settings_language),
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    supportingContent = { Text(currentLangLabel,
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Flag, contentDescription = null,
                        tint = if (isLoggedIn) LocalContentColor.current else Color.Gray) },
                    trailingContent = {
                        if (isLoggedIn) Icon(Icons.Default.ChevronRight, contentDescription = null)
                    },
                    modifier = Modifier
                        .alpha(if (isLoggedIn) 1f else 0.5f)
                        .clickable(enabled = isLoggedIn) {
                        showLangDialog = true
                    }
                )
                if (showLangDialog) {
                    val langOptions = listOf("vi" to langVietnamese, "en" to langEnglish)
                    AlertDialog(
                        onDismissRequest = { showLangDialog = false },
                        title = { Text(stringResource(R.string.settings_lang_dialog_title)) },
                        text = {
                            Column {
                                langOptions.forEach { (code, label) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                authViewModel.updateLanguage(code)
                                                toastMessage = msgLangChanged
                                                showLangDialog = false
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = currentLang == code, onClick = null)
                                        Spacer(Modifier.width(12.dp))
                                        Text(label)
                                    }
                                }
                            }
                        },
                        confirmButton = { }
                    )
                }
                HorizontalDivider()
            }
            // Cập nhật Đã tải
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_downloads),
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    supportingContent = { Text(stringResource(R.string.settings_downloads_desc),
                        color = if (isLoggedIn) Color.Unspecified else Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Download, contentDescription = null,
                        tint = if (isLoggedIn) LocalContentColor.current else Color.Gray) },
                    trailingContent = {
                        if (isLoggedIn) Icon(Icons.Default.ChevronRight, contentDescription = null)
                    },
                    modifier = Modifier
                        .alpha(if (isLoggedIn) 1f else 0.5f)
                        .clickable(enabled = isLoggedIn) {
                            onNavigateToDownloads()
                        }
                )
            }
        }
    }
    if (toastMessage.isNotEmpty()) {
        ToastMessage(toastMessage, 2000)
        LaunchedEffect(toastMessage) {
            delay(2000)
            toastMessage = ""
        }
    }
}