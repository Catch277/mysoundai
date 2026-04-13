package com.example.mysoundai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mysoundai.domain.model.Song
import com.example.mysoundai.service.PlaybackState
import com.example.mysoundai.ui.navigation.bottomNavItems
import com.example.mysoundai.ui.viewmodel.PlayerViewModel

@Composable
fun AppBottomBar(
    navController: NavHostController,
    currentSong: Song?,
    playbackState: PlaybackState,
    playerViewModel: PlayerViewModel,
    onMiniPlayerClick: () -> Unit
) {
    Column {
        if (currentSong != null) {
            MiniPlayer(
                song = currentSong,
                isPlaying = playbackState.isPlaying,
                progress = if (playbackState.duration > 0)
                    (playbackState.currentPosition.toFloat() / playbackState.duration) else 0f,
                onPlayPauseClick = {
                    if (playbackState.isPlaying)
                        playerViewModel.pause() else playerViewModel.resume()
                },
                onClick = { onMiniPlayerClick() }
            )
        }
        NavigationBar(containerColor = Color.Black) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            bottomNavItems.forEach { screen ->
                NavigationBarItem(
                    icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                    label = { screen.titleRes?.let { Text(stringResource(it)) } },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1DB954),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF1DB954),
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}