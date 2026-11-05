package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FormatAlignLeft
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.R
import com.murzik.voidlauncher.data.ClockStyle
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.AccentColor
import com.murzik.voidlauncher.ui.theme.AppAlignment
import com.murzik.voidlauncher.ui.theme.ThemeMode
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidDarkSurface
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass

@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val hwInfo = remember { viewModel.systemManager.getHardwareInfo() }

    var greetingInput by remember(preferences.customGreeting) { mutableStateOf(preferences.customGreeting) }

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
                text = "Настройки Murzik OS",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        HorizontalDivider(color = VoidBorder)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECTION 1: "Жидкое стекло" и Оформление
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.15f
                    )
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Layers, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(22.dp))
                    Text(
                        text = "Стиль интерфейса и стекло",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Theme Mode Selector
                Text(text = "Тема оформления:", fontSize = 13.sp, color = VoidTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                ThemeCard(
                    title = "Матовое стекло (Frosted Glass)",
                    subtitle = "Натуральное стекло с физическим преломлением",
                    isSelected = preferences.themeMode == ThemeMode.LIQUID_GLASS,
                    onClick = { viewModel.setThemeMode(ThemeMode.LIQUID_GLASS) }
                )
                Spacer(modifier = Modifier.height(6.dp))
                ThemeCard(
                    title = "Тёмное стекло (Dark Glass)",
                    subtitle = "Глубокий полупрозрачный интерфейс",
                    isSelected = preferences.themeMode == ThemeMode.DARK_GLASS,
                    onClick = { viewModel.setThemeMode(ThemeMode.DARK_GLASS) }
                )
                Spacer(modifier = Modifier.height(6.dp))
                ThemeCard(
                    title = "Чистый AMOLED",
                    subtitle = "Глубокий чёрный цвет без эффектов, максимальная батарея",
                    isSelected = preferences.themeMode == ThemeMode.AMOLED_BLACK,
                    onClick = { viewModel.setThemeMode(ThemeMode.AMOLED_BLACK) }
                )
                Spacer(modifier = Modifier.height(6.dp))
                ThemeCard(
                    title = "Графит",
                    subtitle = "Нейтральный тёмный титан",
                    isSelected = preferences.themeMode == ThemeMode.GRAPHITE,
                    onClick = { viewModel.setThemeMode(ThemeMode.GRAPHITE) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Accent Colors
                Text(text = "Акцентный цвет:", fontSize = 13.sp, color = VoidTextSecondary)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(AccentColor.values()) { accent ->
                        val isSelected = preferences.accentColor == accent
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(accent.color)
                                .border(if (isSelected) 2.5.dp else 0.dp, Color.White, CircleShape)
                                .clickable { viewModel.setAccentColor(accent) }
                        )
                    }
                }
            }

            // SECTION 2: Папки («Чтобы папки можно было делать как хочешь»)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(18.dp),
                        alpha = 0.15f
                    )
                    .clickable { viewModel.navigateTo(Screen.FOLDER_MANAGER) }
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Folder, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(24.dp))
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = "Конструктор папок",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = VoidTextPrimary
                        )
                        Text(
                            text = if (folders.isEmpty()) "Создавайте свои папки" else "Создано папок: ${folders.size}",
                            fontSize = 12.sp,
                            color = VoidTextSecondary
                        )
                    }
                }
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = null, tint = VoidTextDisabled, modifier = Modifier.size(14.dp))
            }

            // SECTION 3: Часы, Дата и Приветствие
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.15f
                    )
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Schedule, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(22.dp))
                    Text(
                        text = "Часы и Виджеты",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clock Style Selector
                Text(text = "Стиль часов:", fontSize = 13.sp, color = VoidTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                ClockStyle.values().forEach { style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.setClockStyle(style) }
                            .padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = style.title,
                            fontSize = 14.sp,
                            color = if (preferences.clockStyle == style) VoidTextPrimary else VoidTextSecondary,
                            fontWeight = if (preferences.clockStyle == style) FontWeight.Medium else FontWeight.Normal
                        )
                        if (preferences.clockStyle == style) {
                            Text("✓", color = VoidTextPrimary, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Greeting Editor
                Text(text = "Надпись над часами (Приветствие):", fontSize = 13.sp, color = VoidTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = greetingInput,
                    onValueChange = {
                        greetingInput = it
                        viewModel.setCustomGreeting(it)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = VoidTextPrimary,
                        unfocusedTextColor = VoidTextPrimary,
                        focusedBorderColor = VoidTextPrimary,
                        unfocusedBorderColor = VoidBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsSwitchRow(
                    title = "24-часовой формат времени",
                    subtitle = "14:00 вместо 2:00 PM",
                    checked = preferences.clock24Hour,
                    onCheckedChange = { viewModel.setClock24Hour(it) }
                )
            }

            // SECTION 4: Макет и Расположение
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.15f
                    )
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.FormatAlignLeft, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(22.dp))
                    Text(
                        text = "Макет и Типографика",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                SettingsSwitchRow(
                    title = "Режим без иконок (Pure Text)",
                    subtitle = "Только текст приложений для полного фокуса",
                    checked = preferences.textOnlyMode,
                    onCheckedChange = { viewModel.setTextOnlyMode(it) }
                )

                // App Alignment
                Text(text = "Выравнивание приложений на главном:", fontSize = 13.sp, color = VoidTextSecondary, modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppAlignment.values().forEach { align ->
                        val isSelected = preferences.appAlignment == align
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) VoidTextPrimary else Color.White.copy(alpha = 0.08f))
                                .clickable { viewModel.setAppAlignment(align) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = align.title.replace("По ", ""),
                                fontSize = 12.sp,
                                color = if (isSelected) VoidBlack else VoidTextPrimary,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // SECTION 5: Об устройстве Xiaomi 17T & Система
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.15f
                    )
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(22.dp))
                    Text(text = "Об устройстве Xiaomi 17T", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = VoidTextPrimary, modifier = Modifier.padding(start = 10.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))
                InfoRow(label = "Модель", value = hwInfo.model)
                InfoRow(label = "Оболочка", value = hwInfo.osName)
                InfoRow(label = "Операционная система", value = hwInfo.androidVersion)
                InfoRow(label = "Оперативная память (RAM)", value = "${hwInfo.availableRamGb} свободно из ${hwInfo.totalRamGb}")

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { viewModel.restartOnboarding() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Пройти первоначальную настройку заново", fontSize = 13.sp, color = VoidTextPrimary)
                }
            }

            // SECTION 6: Лаунчер по умолчанию & Скрытые приложения
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.15f
                    )
                    .padding(18.dp)
            ) {
                SettingsActionRow(
                    title = "Сделать лаунчером по умолчанию",
                    subtitle = "Назначить основной оболочкой телефона",
                    icon = Icons.Outlined.Home,
                    onClick = { viewModel.appRepository.openDefaultLauncherSettings() }
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))

                SettingsActionRow(
                    title = stringResource(R.string.hidden_apps_title),
                    subtitle = if (hiddenApps.isEmpty()) "Нет скрытых приложений" else "Скрыто: ${hiddenApps.size}",
                    icon = Icons.Outlined.VisibilityOff,
                    onClick = { viewModel.navigateTo(Screen.HIDDEN_APPS) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ThemeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f))
            .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VoidTextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = VoidTextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
        if (isSelected) {
            Text("✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = label, fontSize = 12.sp, color = VoidTextSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = VoidTextPrimary)
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = title, fontSize = 15.sp, color = VoidTextPrimary)
                Text(text = subtitle, fontSize = 12.sp, color = VoidTextSecondary)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = null, tint = VoidTextDisabled, modifier = Modifier.size(13.dp))
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Normal, color = VoidTextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = VoidTextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VoidBlack,
                checkedTrackColor = VoidTextPrimary,
                uncheckedThumbColor = VoidTextSecondary,
                uncheckedTrackColor = VoidDarkSurface,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
