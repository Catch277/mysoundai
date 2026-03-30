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
import com.example.mysoundai.ui.screens.HomeScreen
import com.example.mysoundai.ui.screens.ProfileScreen
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import com.example.mysoundai.ui.screens.LoginScreen
import com.example.mysoundai.ui.screens.RegisterScreen
import com.example.mysoundai.ui.screens.UpdateProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
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
            HomeScreen(viewModel = homeViewModel, paddingValues = paddingValues)
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
            SettingScreen(
                onUpdateProfileClick = {
                    navController.navigate(Screen.UpdateProfile.route)
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
    }
}