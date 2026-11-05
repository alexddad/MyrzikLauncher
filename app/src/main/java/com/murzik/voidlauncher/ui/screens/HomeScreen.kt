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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Folder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.components.AppListItem
import com.murzik.voidlauncher.ui.components.ClockWidget
import com.murzik.voidlauncher.ui.components.FolderListItem
import com.murzik.voidlauncher.ui.theme.AppAlignment
import com.murzik.voidlauncher.ui.theme.ThemeMode
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val pinnedApps by viewModel.pinnedApps.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val batteryStatus by viewModel.systemManager.batteryStatus.collectAsState()

    val pinnedFolders = folders.filter { it.isPinnedToHome }

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
                            if (totalY < -120f) {
                                viewModel.navigateTo(Screen.DRAWER)
                            } else if (totalY > 120f) {
                                viewModel.navigateTo(Screen.CONTROL_CENTER)
                            }
                        } else {
                            if (totalX > 120f) {
                                viewModel.navigateTo(Screen.QUICK_NOTES)
                            } else if (totalX < -120f) {
                                viewModel.navigateTo(Screen.SETTINGS)
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
            // Top Bar: Liquid Glass Status Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Notes Liquid Glass Pill
                Row(
                    modifier = Modifier
                        .liquidGlass(
                            themeMode = preferences.themeMode,
                            shape = CircleShape,
                            alpha = 0.14f
                        )
                        .clickable { viewModel.navigateTo(Screen.QUICK_NOTES) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EditNote,
                        contentDescription = "Notes",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "Заметки",
                        fontSize = 12.sp,
                        color = VoidTextSecondary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // Battery / Control Center Liquid Glass Pill
                Row(
                    modifier = Modifier
                        .liquidGlass(
                            themeMode = preferences.themeMode,
                            shape = CircleShape,
                            alpha = 0.14f
                        )
                        .clickable { viewModel.navigateTo(Screen.CONTROL_CENTER) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${batteryStatus.level}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextSecondary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Icon(
                        imageVector = if (batteryStatus.isCharging) Icons.Outlined.BatteryChargingFull else Icons.Outlined.BatteryFull,
                        contentDescription = null,
                        tint = if (batteryStatus.isCharging) Color(0xFF10B981) else VoidTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Top Area: Clock & Custom Greeting Widget
            if (preferences.clockVisible) {
                ClockWidget(
                    is24Hour = preferences.clock24Hour,
                    clockStyle = preferences.clockStyle,
                    customGreeting = preferences.customGreeting,
                    showScreenTime = preferences.showScreenTime,
                    onClockClick = { viewModel.appRepository.openClockApp() },
                    onDateClick = { viewModel.appRepository.openCalendarApp() },
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Center Area: Pinned Folders & Apps
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = when (preferences.appAlignment) {
                    AppAlignment.CENTER -> Alignment.Center
                    AppAlignment.RIGHT -> Alignment.CenterEnd
                    AppAlignment.LEFT -> Alignment.CenterStart
                }
            ) {
                if (pinnedApps.isEmpty() && pinnedFolders.isEmpty()) {
                    Text(
                        text = "Свайп вверх — Поиск\nСвайп вниз — Центр управления\nСвайп вправо — Заметки\nЗажмите экран — Настройки и Папки",
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
                        // Pinned Folders
                        if (pinnedFolders.isNotEmpty()) {
                            items(pinnedFolders, key = { "folder_${it.id}" }) { folder ->
                                val count = viewModel.getAppsForFolder(folder).size
                                FolderListItem(
                                    folder = folder,
                                    appsCount = count,
                                    textOnlyMode = preferences.textOnlyMode,
                                    hapticEnabled = preferences.hapticFeedback,
                                    themeMode = preferences.themeMode,
                                    onClick = { viewModel.openFolder(folder) },
                                    onLongClick = { viewModel.startEditFolder(folder) },
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        // Pinned Apps
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

            // Bottom Bar: Liquid Glass Search Bar & Shortcuts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Liquid Glass Search bar
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlass(
                            themeMode = preferences.themeMode,
                            shape = CircleShape,
                            alpha = 0.15f
                        )
                        .clickable { viewModel.navigateTo(Screen.DRAWER) }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                    Text(
                        text = "Поиск или калькулятор…",
                        color = VoidTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                // Create Folder shortcut
                IconButton(
                    onClick = { viewModel.startCreateFolder() },
                    modifier = Modifier
                        .liquidGlass(
                            themeMode = preferences.themeMode,
                            shape = CircleShape,
                            alpha = 0.15f
                        )
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CreateNewFolder,
                        contentDescription = "New Folder",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                // Control Center shortcut
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.CONTROL_CENTER) },
                    modifier = Modifier
                        .liquidGlass(
                            themeMode = preferences.themeMode,
                            shape = CircleShape,
                            alpha = 0.15f
                        )
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Control Center",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                // Settings shortcut
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                    modifier = Modifier
                        .liquidGlass(
                            themeMode = preferences.themeMode,
                            shape = CircleShape,
                            alpha = 0.15f
                        )
                        .size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = VoidTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
