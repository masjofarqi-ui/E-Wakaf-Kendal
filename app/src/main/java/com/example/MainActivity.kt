package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.repository.WakafRepository
import com.example.ui.screens.DaftarDataScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentViewerScreen
import com.example.ui.screens.InputDataScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WakafViewModel
import com.example.ui.viewmodel.WakafViewModelFactory

// In-Memory Backstack Safe Router Screen Definition
sealed class Screen {
    object Dashboard : Screen()
    object InputData : Screen()
    object DaftarData : Screen()
    data class DocumentViewer(val landId: Long) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize database and repository inside Compose with safe context boundaries
                val context = LocalContext.current.applicationContext as Application
                val database = AppDatabase.getDatabase(context)
                val repository = WakafRepository(database.wakafDao())
                val factory = WakafViewModelFactory(context, repository)
                val viewModel: WakafViewModel = viewModel(factory = factory)

                // Simple custom in-memory routing engine with support for safe back navigations
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
                val navigationBackstack = remember { mutableStateListOf<Screen>() }

                fun navigateTo(screen: Screen) {
                    navigationBackstack.add(currentScreen)
                    currentScreen = screen
                }

                fun navigateBack() {
                    if (navigationBackstack.isNotEmpty()) {
                        currentScreen = navigationBackstack.removeLast()
                    } else {
                        currentScreen = Screen.Dashboard
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Smooth modern slide crossfade transaction animations
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith 
                                fadeOut(animationSpec = tween(220))
                            },
                            label = "screen_routing"
                        ) { screen ->
                            when (screen) {
                                is Screen.Dashboard -> {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        onNavigateToInput = { navigateTo(Screen.InputData) },
                                        onNavigateToDaftar = { navigateTo(Screen.DaftarData) },
                                        onNavigateToDetail = { id -> navigateTo(Screen.DocumentViewer(id)) }
                                    )
                                }
                                is Screen.InputData -> {
                                    InputDataScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { navigateBack() },
                                        onSaveSuccess = { savedId ->
                                            // Take user straight to the generated 9 letters view
                                            navigateTo(Screen.DocumentViewer(savedId))
                                        }
                                    )
                                }
                                is Screen.DaftarData -> {
                                    DaftarDataScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { navigateBack() },
                                        onNavigateToDetail = { id -> navigateTo(Screen.DocumentViewer(id)) },
                                        onNavigateToInput = { navigateTo(Screen.InputData) }
                                    )
                                }
                                is Screen.DocumentViewer -> {
                                    DocumentViewerScreen(
                                        viewModel = viewModel,
                                        landId = screen.landId,
                                        onNavigateBack = { navigateBack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
