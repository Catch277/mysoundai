package com.example.mysoundai

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.mysoundai.ui.components.MiniPlayer
import com.example.mysoundai.ui.navigation.AppNavigation
import com.example.mysoundai.ui.navigation.bottomNavItems
import com.example.mysoundai.ui.screens.NowPlayingScreen
import com.example.mysoundai.ui.viewmodel.AuthViewModel
import com.example.mysoundai.ui.viewmodel.DownloadViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        handleSpotifyCallback(intent)

        val factory = viewModelFactory {
            initializer {
                DownloadViewModel(AppContainer.downloadRepository)
            }
            initializer {
                PlayerViewModel(AppContainer.musicController)
            }
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
            val downloadViewModel: DownloadViewModel = viewModel(factory = factory)
            val playerViewModel: PlayerViewModel = viewModel(factory = factory)
            MySoundAITheme(authViewModel = authViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(homeViewModel = homeViewModel, authViewModel = authViewModel, downloadViewModel = downloadViewModel, playerViewModel = playerViewModel)
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
fun MainScreen(homeViewModel: HomeViewModel,
               authViewModel: AuthViewModel,
               downloadViewModel: DownloadViewModel,
               playerViewModel: PlayerViewModel
) {
    val navController = rememberNavController()

    val playbackState by playerViewModel.playbackState.collectAsState()
    val currentSong by playerViewModel.currentSong.collectAsState()

    var showNowPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(currentSong) {
        if (currentSong == null) showNowPlaying = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            //
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
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
                            onClick = { showNowPlaying = true }
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
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                downloadViewModel = downloadViewModel,
                playerViewModel = playerViewModel,
                paddingValues = innerPadding
            )
        }
        AnimatedVisibility(
            visible = showNowPlaying,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NowPlayingScreen(
                playerViewModel = playerViewModel,
                onClose = { showNowPlaying = false }
            )
        }
    }
}




