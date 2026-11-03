package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.BatteryStd
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidDarkSurface
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary

@Composable
fun SettingsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val hiddenApps by viewModel.hiddenApps.collectAsState()
    val hwInfo = remember { viewModel.systemManager.getHardwareInfo() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
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
            // "About Device" Card (Xiaomi 17T)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VoidCardSurface)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = VoidTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Об устройстве",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(label = "Модель", value = hwInfo.model)
                InfoRow(label = "Оболочка", value = hwInfo.osName)
                InfoRow(label = "Операционная система", value = hwInfo.androidVersion)
                InfoRow(label = "Оперативная память (RAM)", value = "${hwInfo.availableRamGb} свободно из ${hwInfo.totalRamGb}")

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { viewModel.restartOnboarding() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Пройти первоначальную настройку заново", fontSize = 13.sp, color = VoidTextPrimary)
                }
            }

            // Default Launcher Quick Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VoidCardSurface)
                    .clickable { viewModel.appRepository.openDefaultLauncherSettings() }
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = null,
                        tint = VoidTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = stringResource(R.string.set_default_launcher),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = VoidTextPrimary
                        )
                        Text(
                            text = "Назначить основной оболочкой телефона",
                            fontSize = 12.sp,
                            color = VoidTextSecondary
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                    contentDescription = null,
                    tint = VoidTextDisabled,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Personalization Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VoidCardSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Оформление и дисплей",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VoidTextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                SettingsSwitchRow(
                    title = stringResource(R.string.text_only_mode),
                    subtitle = "Минималистичный текст без иконок",
                    checked = preferences.textOnlyMode,
                    onCheckedChange = { viewModel.setTextOnlyMode(it) }
                )

                SettingsSwitchRow(
                    title = stringResource(R.string.show_clock),
                    subtitle = "Часы и дата на домашнем экране",
                    checked = preferences.clockVisible,
                    onCheckedChange = { viewModel.setClockVisible(it) }
                )

                if (preferences.clockVisible) {
                    SettingsSwitchRow(
                        title = stringResource(R.string.clock_format_24),
                        subtitle = "24-часовой формат",
                        checked = preferences.clock24Hour,
                        onCheckedChange = { viewModel.setClock24Hour(it) }
                    )
                }

                SettingsActionRow(
                    title = "Системный дисплей Xiaomi",
                    subtitle = "Частота обновления (120Гц), яркость",
                    icon = Icons.Outlined.DisplaySettings,
                    onClick = { viewModel.systemManager.openDisplaySettings() }
                )
            }

            // Sound and Controls Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VoidCardSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Звук и тактильный отклик",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VoidTextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                SettingsSwitchRow(
                    title = stringResource(R.string.haptic_feedback),
                    subtitle = "Вибрация при кликах и удержании",
                    checked = preferences.hapticFeedback,
                    onCheckedChange = { viewModel.setHapticFeedback(it) }
                )

                SettingsActionRow(
                    title = "Системные звуки и вибрация",
                    subtitle = "Мелодии вызова, уведомления",
                    icon = Icons.Outlined.VolumeUp,
                    onClick = { viewModel.systemManager.openSoundSettings() }
                )
            }

            // Privacy & Hidden Apps
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VoidCardSurface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Приватность и безопасность",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VoidTextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                SettingsActionRow(
                    title = stringResource(R.string.hidden_apps_title),
                    subtitle = if (hiddenApps.isEmpty()) "Нет скрытых приложений" else "Скрыто приложений: ${hiddenApps.size}",
                    icon = Icons.Outlined.VisibilityOff,
                    onClick = { viewModel.navigateTo(Screen.HIDDEN_APPS) }
                )

                SettingsActionRow(
                    title = "Управление питанием Xiaomi",
                    subtitle = "Экономия энергии, статистика батареи",
                    icon = Icons.Outlined.BatteryStd,
                    onClick = { viewModel.systemManager.openBatterySettings() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 12.sp, color = VoidTextSecondary)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Normal, color = VoidTextPrimary)
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
            .padding(vertical = 10.dp),
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
            .padding(vertical = 10.dp),
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
