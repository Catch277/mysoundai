package com.example.mysoundai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Home: Screen("home", "Trang chủ", Icons.Default.Home)
    object Search: Screen("search", "Khám phá", Icons.Default.Search)
    object Library: Screen("library", "Thư viện", Icons.Default.LibraryMusic)
    object Profile: Screen("profile", "Cá nhân", Icons.Default.Person)
    object Register: Screen("register", "Đăng ký")
    object Login: Screen("login", "Đăng nhập")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Library,
    Screen.Profile
)