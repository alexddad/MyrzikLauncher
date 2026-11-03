package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.components.AppListItem
import com.murzik.voidlauncher.ui.components.ClockWidget
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val pinnedApps by viewModel.pinnedApps.collectAsState()
    val batteryStatus by viewModel.systemManager.batteryStatus.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                var totalX = 0f
                var totalY = 0f
                detectDragGestures(
                    onDragStart = {
                        totalX = 0f
                        totalY = 0f
                    },
                    onDragEnd = {
                        if (abs(totalY) > abs(totalX)) {
                            // Vertical gestures
                            if (totalY < -120f) {
                                viewModel.navigateTo(Screen.DRAWER) // Swipe UP -> Drawer
                            } else if (totalY > 120f) {
                                viewModel.navigateTo(Screen.CONTROL_CENTER) // Swipe DOWN -> MurzikOS Control Center
                            }
                        } else {
                            // Horizontal gestures
                            if (totalX > 120f) {
                                viewModel.navigateTo(Screen.QUICK_NOTES) // Swipe RIGHT -> Quick Notes
                            } else if (totalX < -120f) {
                                viewModel.navigateTo(Screen.SETTINGS) // Swipe LEFT -> MurzikOS Settings
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalX += dragAmount.x
                        totalY += dragAmount.y
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
            // Top Bar: Battery status pill + Quick access
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notes Shortcut
                Row(
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.QUICK_NOTES) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EditNote,
                        contentDescription = "Notes",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Заметки",
                        fontSize = 13.sp,
                        color = VoidTextSecondary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // Battery & Control Center Pill
                Row(
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.CONTROL_CENTER) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${batteryStatus.level}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextSecondary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Icon(
                        imageVector = if (batteryStatus.isCharging) Icons.Outlined.BatteryChargingFull else Icons.Outlined.BatteryFull,
                        contentDescription = null,
                        tint = if (batteryStatus.isCharging) Color(0xFF4CAF50) else VoidTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Clock & Date Widget
            if (preferences.clockVisible) {
                ClockWidget(
                    is24Hour = preferences.clock24Hour,
                    onClockClick = { viewModel.appRepository.openClockApp() },
                    onDateClick = { viewModel.appRepository.openCalendarApp() },
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
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
                        text = "Свайп вверх — Поиск\nСвайп вниз — Центр управления\nСвайп вправо — Заметки",
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

            // Bottom Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.DRAWER) }
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

                // Control Center icon
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.CONTROL_CENTER) }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Control Center",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Settings icon
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
