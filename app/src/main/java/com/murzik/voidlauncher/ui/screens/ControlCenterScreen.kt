package com.murzik.voidlauncher.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AirplanemodeActive
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.ThemeMode
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidDarkSurface
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass

@Composable
fun ControlCenterScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val isFlashlightOn by viewModel.systemManager.isFlashlightOn.collectAsState()
    val batteryStatus by viewModel.systemManager.batteryStatus.collectAsState()
    val preferences by viewModel.preferences.collectAsState()

    val maxVol = remember { viewModel.systemManager.getMaxMediaVolume() }
    var currentVol by remember {
        mutableFloatStateOf(viewModel.systemManager.getMediaVolume().toFloat())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = VoidTextSecondary
                )
            }
            Text(
                text = "Центр Управления Murzik OS",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        HorizontalDivider(color = VoidBorder)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Battery Status Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.16f
                    )
                    .clickable { viewModel.systemManager.openBatterySettings() }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (batteryStatus.isCharging) Icons.Outlined.BatteryChargingFull else Icons.Outlined.BatteryFull,
                        contentDescription = null,
                        tint = if (batteryStatus.isCharging) Color(0xFF4CAF50) else VoidTextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = "${batteryStatus.level}%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            color = VoidTextPrimary
                        )
                        Text(
                            text = if (batteryStatus.isCharging) "Идет быстрая зарядка Xiaomi" else "Питание от батареи",
                            fontSize = 13.sp,
                            color = VoidTextSecondary
                        )
                    }
                }
                Text(
                    text = "Питание →",
                    fontSize = 13.sp,
                    color = VoidTextSecondary
                )
            }

            // Media Player Widget
            val mediaState by viewModel.systemManager.mediaState.collectAsState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(18.dp),
                        alpha = 0.14f
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = "🎵", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text(text = mediaState.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VoidTextPrimary)
                        Text(text = if (mediaState.isPlaying) "Воспроизведение • Xiaomi 17T" else "На паузе", fontSize = 11.sp, color = VoidTextSecondary)
                    }
                }
                IconButton(onClick = { viewModel.systemManager.toggleMediaPlayback() }) {
                    Text(text = if (mediaState.isPlaying) "⏸" else "▶", fontSize = 20.sp, color = VoidTextPrimary)
                }
            }

            // Quick Tiles Grid (2x2) with Spring Animations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTile(
                    title = "Wi-Fi",
                    subtitle = "Сети",
                    icon = Icons.Outlined.Wifi,
                    isActive = false,
                    onClick = { viewModel.systemManager.openWifiSettings() },
                    modifier = Modifier.weight(1f)
                )

                QuickTile(
                    title = "Bluetooth",
                    subtitle = "Устройства",
                    icon = Icons.Outlined.Bluetooth,
                    isActive = false,
                    onClick = { viewModel.systemManager.openBluetoothSettings() },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTile(
                    title = "Фонарик",
                    subtitle = if (isFlashlightOn) "ВКЛЮЧЕН" else "Выключен",
                    icon = if (isFlashlightOn) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff,
                    isActive = isFlashlightOn,
                    onClick = { viewModel.systemManager.toggleFlashlight() },
                    modifier = Modifier.weight(1f)
                )

                QuickTile(
                    title = "Режим полета",
                    subtitle = "Сеть",
                    icon = Icons.Outlined.AirplanemodeActive,
                    isActive = false,
                    onClick = { viewModel.systemManager.openAirplaneSettings() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Media Volume Control Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.16f
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.VolumeUp,
                            contentDescription = null,
                            tint = VoidTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Громкость медиа",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = VoidTextPrimary,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    Text(
                        text = "${(currentVol / maxVol * 100).toInt()}%",
                        fontSize = 13.sp,
                        color = VoidTextSecondary
                    )
                }

                Slider(
                    value = currentVol,
                    onValueChange = {
                        currentVol = it
                        viewModel.systemManager.setMediaVolume(it.toInt())
                    },
                    valueRange = 0f..maxVol.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = VoidTextPrimary,
                        activeTrackColor = VoidTextPrimary,
                        inactiveTrackColor = VoidDarkSurface
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // System Tools Row (Calculator & Storage)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickTile(
                    title = "Калькулятор",
                    subtitle = "Системный",
                    icon = Icons.Outlined.Calculate,
                    isActive = false,
                    onClick = { viewModel.navigateTo(Screen.CALCULATOR) },
                    modifier = Modifier.weight(1f)
                )

                QuickTile(
                    title = "Память",
                    subtitle = "Хранилище",
                    icon = Icons.Outlined.SdStorage,
                    isActive = false,
                    onClick = { viewModel.navigateTo(Screen.STORAGE_MANAGER) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Settings Tile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VoidCardSurface)
                    .clickable { viewModel.navigateTo(Screen.SETTINGS) }
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = VoidTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Все параметры Murzik OS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Text(
                    text = "→",
                    fontSize = 16.sp,
                    color = VoidTextSecondary
                )
            }
        }
    }
}

@Composable
private fun QuickTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "TileScale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isActive) VoidTextPrimary else VoidCardSurface,
        animationSpec = tween(durationMillis = 220),
        label = "TileBgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isActive) VoidBlack else VoidTextPrimary,
        animationSpec = tween(durationMillis = 220),
        label = "TileContentColor"
    )

    val subColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFF333333) else VoidTextSecondary,
        animationSpec = tween(durationMillis = 220),
        label = "TileSubColor"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .liquidGlass(
                themeMode = ThemeMode.LIQUID_GLASS,
                shape = RoundedCornerShape(18.dp),
                alpha = if (isActive) 0.95f else 0.12f
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = subColor
        )
    }
}
