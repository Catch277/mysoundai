package com.example.mysoundai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mysoundai.ui.screens.HomeScreen
import com.example.mysoundai.ui.theme.MySoundAITheme
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mysoundai.di.AppContainer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = viewModelFactory {
            initializer {
                HomeViewModel(AppContainer.musicRepository)
            }
        }

        setContent {
            MySoundAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val homeViewModel: HomeViewModel = viewModel(factory = factory)
                    HomeScreen(viewModel = homeViewModel)
                }
            }
        }
    }
}