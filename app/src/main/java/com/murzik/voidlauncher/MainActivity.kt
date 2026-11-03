package com.murzik.voidlauncher

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.components.AppActionsBottomSheet
import com.murzik.voidlauncher.ui.screens.AppDrawerScreen
import com.murzik.voidlauncher.ui.screens.HiddenAppsScreen
import com.murzik.voidlauncher.ui.screens.HomeScreen
import com.murzik.voidlauncher.ui.screens.SettingsScreen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidLauncherTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force hardware acceleration for budget GPU chips
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        enableEdgeToEdge()

        setContent {
            VoidLauncherTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val selectedApp by viewModel.selectedAppForMenu.collectAsState()

                BackHandler(enabled = currentScreen != Screen.HOME) {
                    viewModel.handleBackPressed()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VoidBlack
                ) {
                    // Ultra-fast 100ms fade transition for zero-stutter navigation
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(100)) togetherWith fadeOut(animationSpec = tween(100))
                        },
                        label = "FastScreenTransition"
                    ) { screen ->
                        when (screen) {
                            Screen.HOME -> HomeScreen(viewModel = viewModel)
                            Screen.DRAWER -> AppDrawerScreen(viewModel = viewModel)
                            Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            Screen.HIDDEN_APPS -> HiddenAppsScreen(viewModel = viewModel)
                        }
                    }

                    // Context actions bottom sheet
                    selectedApp?.let { app ->
                        AppActionsBottomSheet(
                            app = app,
                            onDismiss = { viewModel.closeAppMenu() },
                            onTogglePin = { viewModel.togglePin(app) },
                            onToggleHide = { viewModel.toggleHide(app) },
                            onAppInfo = {
                                viewModel.appRepository.openAppInfo(app.packageName)
                                viewModel.closeAppMenu()
                            },
                            onUninstall = {
                                viewModel.appRepository.uninstallApp(app.packageName)
                                viewModel.closeAppMenu()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadApps()
    }
}
