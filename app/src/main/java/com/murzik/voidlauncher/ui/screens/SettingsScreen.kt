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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.R
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
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
                text = stringResource(R.string.settings_title),
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
                .padding(vertical = 12.dp)
        ) {
            // Set Default Launcher button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.appRepository.openDefaultLauncherSettings() }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = VoidTextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(
                        text = stringResource(R.string.set_default_launcher),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextPrimary
                    )
                    Text(
                        text = "Сделать домашним экраном телефона",
                        fontSize = 13.sp,
                        color = VoidTextSecondary
                    )
                }
            }

            HorizontalDivider(color = VoidBorder, modifier = Modifier.padding(vertical = 8.dp))

            // Appearance section
            Text(
                text = stringResource(R.string.appearance),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            SettingsSwitchRow(
                title = stringResource(R.string.text_only_mode),
                subtitle = "Скрыть стандартные иконки приложений",
                checked = preferences.textOnlyMode,
                onCheckedChange = { viewModel.setTextOnlyMode(it) }
            )

            SettingsSwitchRow(
                title = stringResource(R.string.show_clock),
                subtitle = "Часы и дата на главном экране",
                checked = preferences.clockVisible,
                onCheckedChange = { viewModel.setClockVisible(it) }
            )

            if (preferences.clockVisible) {
                SettingsSwitchRow(
                    title = stringResource(R.string.clock_format_24),
                    subtitle = "Использовать 24-часовой формат времени",
                    checked = preferences.clock24Hour,
                    onCheckedChange = { viewModel.setClock24Hour(it) }
                )
            }

            HorizontalDivider(color = VoidBorder, modifier = Modifier.padding(vertical = 8.dp))

            // Interactions section
            Text(
                text = "Поведение и приватность",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            SettingsSwitchRow(
                title = stringResource(R.string.haptic_feedback),
                subtitle = "Легкая вибрация при нажатии и удержании",
                checked = preferences.hapticFeedback,
                onCheckedChange = { viewModel.setHapticFeedback(it) }
            )

            // Hidden Apps Navigation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.HIDDEN_APPS) }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        tint = VoidTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = stringResource(R.string.hidden_apps_title),
                            fontSize = 16.sp,
                            color = VoidTextPrimary
                        )
                        Text(
                            text = if (hiddenApps.isEmpty()) "Нет скрытых приложений" else "Скрыто: ${hiddenApps.size}",
                            fontSize = 13.sp,
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

            Spacer(modifier = Modifier.height(24.dp))
        }
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
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = VoidTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = VoidTextSecondary
            )
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
