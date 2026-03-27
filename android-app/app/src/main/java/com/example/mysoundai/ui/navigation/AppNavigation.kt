package com.example.mysoundai.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mysoundai.ui.screens.HomeScreen
import com.example.mysoundai.ui.screens.ProfileScreen
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mysoundai.di.AppContainer
@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: HomeViewModel,
    paddingValues: PaddingValues
) {
    val authViewModel:  AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(AppContainer.authRepository) as T
            }
        }
    )
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues),
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel, paddingValues = paddingValues)
        }
        composable(Screen.Search.route) { /* Khám phá */ }
        composable(Screen.Library.route) { /* Thư viện */ }
        composable(Screen.Profile.route) { ProfileScreen(
            authViewModel = authViewModel,
            onSettingsClick = { /* Handle settings click */ }
        ) }
    }
}