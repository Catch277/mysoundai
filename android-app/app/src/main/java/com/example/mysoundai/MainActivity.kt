package com.example.mysoundai

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mysoundai.ui.theme.MySoundAITheme
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mysoundai.di.AppContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.example.mysoundai.ui.navigation.AppNavigation
import com.example.mysoundai.ui.navigation.bottomNavItems
import com.example.mysoundai.ui.viewmodel.AuthViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        handleSpotifyCallback(intent)

        val factory = viewModelFactory {
            initializer {
                HomeViewModel(AppContainer.musicRepository)
            }
            initializer {
                AuthViewModel(
                    AppContainer.authRepository,
                    AppContainer.themePreference,
                    AppContainer.languagePreference
                    )
            }
        }

        setContent {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            MySoundAITheme(authViewModel = authViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(homeViewModel = homeViewModel, authViewModel = authViewModel)
                }
            }
        }
    }
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//        handleSpotifyCallback(intent)
//    }

//    private fun handleSpotifyCallback(intent: Intent?) {
//        val data: Uri? = intent?.data
//        if (data != null && data.toString().startsWith("mysoundai://callback")) {
//            val code = data.getQueryParameter("code")
//            if (code != null) {
//                // ĐÂY LÀ DÒNG SẼ HIỆN TRONG LOGCAT
//                android.util.Log.d("SpotifyAuth", "Mã Code nhận được: $code")
//            } else {
//                val error = data.getQueryParameter("error")
//                android.util.Log.e("SpotifyAuth", "Lỗi từ Spotify: $error")
//            }
//        }
//    }
}

@Composable
fun MainScreen(homeViewModel: HomeViewModel, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = null)}},
                        label = { screen.titleRes?.let {Text(stringResource(it))}},
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
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            homeViewModel = homeViewModel,
            authViewModel = authViewModel,
            paddingValues = innerPadding
        )
    }
    }




