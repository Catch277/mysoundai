package com.example.mysoundai.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mysoundai.ui.screens.DownloadedMusicScreen
import com.example.mysoundai.ui.screens.HomeScreen
import com.example.mysoundai.ui.screens.LibraryScreen
import com.example.mysoundai.ui.screens.ProfileScreen
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import com.example.mysoundai.ui.screens.LoginScreen
import com.example.mysoundai.ui.screens.PlaylistDetailScreen
import com.example.mysoundai.ui.screens.RegisterScreen
import com.example.mysoundai.ui.screens.SettingsScreen
import com.example.mysoundai.ui.screens.UpdateProfileScreen
import com.example.mysoundai.ui.viewmodel.DownloadViewModel
import com.example.mysoundai.ui.viewmodel.LibraryViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    downloadViewModel: DownloadViewModel,
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues),
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                downloadViewModel = downloadViewModel,
                playerViewModel = playerViewModel,
                libraryViewModel = libraryViewModel,
                paddingValues = paddingValues
            )
        }
        composable(Screen.Search.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )

                    Text(
                        text = "Coming Soon",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        // LIBRARY SECTION //
        composable(Screen.Library.route) {
            LibraryScreen(
                libraryViewModel = libraryViewModel,
                playerViewModel = playerViewModel,
                downloadViewModel = downloadViewModel,
                homeViewModel = homeViewModel,
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                },
                onNavigateToPlaylistDetail = { playlistId ->
                    navController.navigate("${Screen.PlaylistDetail.route}/$playlistId")
                }
            )
        }
        composable(
            route = "${Screen.PlaylistDetail.route}/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getInt("playlistId")  ?: -1
            PlaylistDetailScreen(
                playlistId = playlistId,
                libraryViewModel = libraryViewModel,
                downloadViewModel = downloadViewModel,
                playerViewModel = playerViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        // LIBRARY SECTION //
        // PROFILE SECTION //
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onBack = {
                    navController.popBackStack() },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                authViewModel = authViewModel,
                onNavigateToUpdateProfile = {
                    navController.navigate(Screen.UpdateProfile.route)
                },
                onNavigateToDownloads = {
                    navController.navigate(Screen.Downloads.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.UpdateProfile.route) {
            UpdateProfileScreen(
                authViewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Downloads.route) {
            DownloadedMusicScreen(
                viewModel = downloadViewModel,
                playerViewModel = playerViewModel,
                libraryViewModel = libraryViewModel,
                onNavigateToExplore = {
                    navController.navigate(Screen.Home.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        // PROFILE SECTION //
    }
}