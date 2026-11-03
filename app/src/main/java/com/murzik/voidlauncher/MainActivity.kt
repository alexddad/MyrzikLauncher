package com.murzik.voidlauncher

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import com.murzik.voidlauncher.ui.screens.ControlCenterScreen
import com.murzik.voidlauncher.ui.screens.HiddenAppsScreen
import com.murzik.voidlauncher.ui.screens.HomeScreen
import com.murzik.voidlauncher.ui.screens.OnboardingScreen
import com.murzik.voidlauncher.ui.screens.QuickNotesScreen
import com.murzik.voidlauncher.ui.screens.SettingsScreen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidLauncherTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        enableEdgeToEdge()

        setContent {
            VoidLauncherTheme {
                val preferences by viewModel.preferences.collectAsState()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val selectedApp by viewModel.selectedAppForMenu.collectAsState()

                val shouldShowOnboarding = !preferences.isFirstLaunchCompleted || currentScreen == Screen.ONBOARDING

                BackHandler(enabled = currentScreen != Screen.HOME && !shouldShowOnboarding) {
                    viewModel.handleBackPressed()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VoidBlack
                ) {
                    if (shouldShowOnboarding) {
                        OnboardingScreen(viewModel = viewModel)
                    } else {
                        // Smooth, spatial physics-based 120Hz transitions
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                val springSpec = spring<Float>(dampingRatio = 0.85f, stiffness = 420f)
                                when (targetState) {
                                    Screen.DRAWER -> {
                                        (slideInVertically(spring(dampingRatio = 0.85f, stiffness = 420f)) { it / 3 } + fadeIn(springSpec) + scaleIn(springSpec, 0.94f))
                                            .togetherWith(fadeOut(springSpec) + scaleOut(springSpec, 0.96f))
                                    }
                                    Screen.CONTROL_CENTER -> {
                                        (slideInVertically(spring(dampingRatio = 0.85f, stiffness = 420f)) { -it / 3 } + fadeIn(springSpec))
                                            .togetherWith(slideOutVertically(spring(dampingRatio = 0.85f, stiffness = 420f)) { -it / 4 } + fadeOut(springSpec))
                                    }
                                    Screen.QUICK_NOTES -> {
                                        (slideInHorizontally(spring(dampingRatio = 0.85f, stiffness = 420f)) { -it / 3 } + fadeIn(springSpec))
                                            .togetherWith(slideOutHorizontally(spring(dampingRatio = 0.85f, stiffness = 420f)) { -it / 4 } + fadeOut(springSpec))
                                    }
                                    Screen.SETTINGS, Screen.HIDDEN_APPS -> {
                                        (slideInHorizontally(spring(dampingRatio = 0.85f, stiffness = 420f)) { it / 3 } + fadeIn(springSpec))
                                            .togetherWith(slideOutHorizontally(spring(dampingRatio = 0.85f, stiffness = 420f)) { it / 4 } + fadeOut(springSpec))
                                    }
                                    Screen.HOME -> {
                                        (fadeIn(springSpec) + scaleIn(springSpec, 0.95f))
                                            .togetherWith(fadeOut(springSpec) + scaleOut(springSpec, 0.97f))
                                    }
                                    else -> fadeIn(springSpec) togetherWith fadeOut(springSpec)
                                }
                            },
                            label = "MurzikOSSpatialAnimation"
                        ) { screen ->
                            when (screen) {
                                Screen.HOME -> HomeScreen(viewModel = viewModel)
                                Screen.DRAWER -> AppDrawerScreen(viewModel = viewModel)
                                Screen.CONTROL_CENTER -> ControlCenterScreen(viewModel = viewModel)
                                Screen.QUICK_NOTES -> QuickNotesScreen(viewModel = viewModel)
                                Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                                Screen.HIDDEN_APPS -> HiddenAppsScreen(viewModel = viewModel)
                                Screen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
                            }
                        }

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
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadApps()
    }
}
