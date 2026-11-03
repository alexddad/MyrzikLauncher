package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.components.AppListItem
import com.murzik.voidlauncher.ui.components.ClockWidget
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val pinnedApps by viewModel.pinnedApps.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onDragEnd = {
                        if (totalDrag < -100f) {
                            // Swiped Up -> Open Drawer
                            viewModel.navigateTo(Screen.DRAWER)
                        } else if (totalDrag > 100f) {
                            // Swiped Down -> Expand Notifications
                            viewModel.appRepository.expandNotificationShade()
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    }
                )
            }
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    viewModel.navigateTo(Screen.SETTINGS)
                }
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Clock and Date
            if (preferences.clockVisible) {
                ClockWidget(
                    is24Hour = preferences.clock24Hour,
                    onClockClick = { viewModel.appRepository.openClockApp() },
                    onDateClick = { viewModel.appRepository.openCalendarApp() },
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Middle: Pinned Apps List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (pinnedApps.isEmpty()) {
                    Text(
                        text = "Свайпните вверх для поиска\nили зажмите приложение, чтобы закрепить его здесь",
                        color = VoidTextDisabled,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        items(pinnedApps, key = { it.id }) { app ->
                            AppListItem(
                                app = app,
                                textOnlyMode = preferences.textOnlyMode,
                                hapticEnabled = preferences.hapticFeedback,
                                onClick = { viewModel.launchApp(app) },
                                onLongClick = { viewModel.openAppMenu(app) }
                            )
                        }
                    }
                }
            }

            // Bottom Bar: Search prompt + Settings shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .combinedClickable(
                            onClick = { viewModel.navigateTo(Screen.DRAWER) }
                        )
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Поиск…",
                        color = VoidTextSecondary,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.navigateTo(Screen.SETTINGS) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
