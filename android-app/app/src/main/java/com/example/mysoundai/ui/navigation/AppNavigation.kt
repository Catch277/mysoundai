package com.example.mysoundai.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mysoundai.ui.screens.DownloadedMusicScreen
import com.example.mysoundai.ui.screens.HomeScreen
import com.example.mysoundai.ui.screens.ProfileScreen
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import com.example.mysoundai.ui.screens.LoginScreen
import com.example.mysoundai.ui.screens.RegisterScreen
import com.example.mysoundai.ui.screens.SettingsScreen
import com.example.mysoundai.ui.screens.UpdateProfileScreen
import com.example.mysoundai.ui.viewmodel.DownloadViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    downloadViewModel: DownloadViewModel,
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
            HomeScreen(viewModel = homeViewModel,
                downloadViewModel = downloadViewModel,
                paddingValues = paddingValues)
        }
        composable(Screen.Search.route) { /* Khám phá */ }
        composable(Screen.Library.route) { /* Thư viện */ }
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
        // PROFILE SECTION //
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