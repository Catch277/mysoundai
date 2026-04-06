package com.example.mysoundai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.example.mysoundai.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int? = null,
    val icon: ImageVector? = null
) {
    object Home         : Screen("home",          R.string.nav_home,           Icons.Default.Home)
    object Search       : Screen("search",        R.string.nav_search,         Icons.Default.Search)
    object Library      : Screen("library",       R.string.nav_library,        Icons.Default.LibraryMusic)
    object Profile      : Screen("profile",       R.string.nav_profile,        Icons.Default.Person)
    object Register     : Screen("register",      R.string.nav_register)
    object Login        : Screen("login",         R.string.nav_login)
    object Settings     : Screen("settings",      R.string.nav_settings)
    object UpdateProfile: Screen("updateProfile", R.string.nav_update_profile)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Library,
    Screen.Profile
)