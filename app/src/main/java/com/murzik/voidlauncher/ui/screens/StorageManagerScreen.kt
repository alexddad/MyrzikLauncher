package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass

@Composable
fun StorageManagerScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val hwInfo = remember { viewModel.systemManager.getHardwareInfo() }
    val storage = hwInfo.storageInfo

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
            IconButton(onClick = { viewModel.navigateTo(Screen.SETTINGS) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = VoidTextSecondary
                )
            }
            Text(
                text = "Хранилище и Память",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = VoidTextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        HorizontalDivider(color = VoidBorder)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Storage Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.14f
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Постоянная память", fontSize = 13.sp, color = VoidTextSecondary)
                        Text(
                            text = "${storage.usedGb} ГБ из ${storage.totalGb} ГБ",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            color = VoidTextPrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = "${storage.usedPercent}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { (storage.usedPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Свободно ${storage.availableGb} ГБ",
                    fontSize = 12.sp,
                    color = VoidTextSecondary
                )
            }

            // RAM Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.14f
                    )
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Memory, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(22.dp))
                    Text(
                        text = "Оперативная память (RAM)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Всего", fontSize = 12.sp, color = VoidTextSecondary)
                        Text(text = hwInfo.totalRamGb, fontSize = 16.sp, color = VoidTextPrimary, fontWeight = FontWeight.Medium)
                    }
                    Column {
                        Text(text = "Свободно", fontSize = 12.sp, color = VoidTextSecondary)
                        Text(text = hwInfo.availableRamGb, fontSize = 16.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Storage Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        themeMode = preferences.themeMode,
                        shape = RoundedCornerShape(20.dp),
                        alpha = 0.14f
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Категории файлов", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VoidTextSecondary)

                CategoryRow(name = "Приложения и игры", value = "Установлено на устройстве", icon = Icons.Outlined.SdStorage)
                CategoryRow(name = "Система Xiaomi", value = "Android 15 Core", icon = Icons.Outlined.Memory)
                CategoryRow(name = "Документы и медиа", value = "Загрузки и медиафайлы", icon = Icons.Outlined.Folder)
            }

            // Action Button
            Button(
                onClick = { viewModel.systemManager.openStorageSettings() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(imageVector = Icons.Outlined.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Системное хранилище Xiaomi", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun CategoryRow(
    name: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = name, fontSize = 14.sp, color = VoidTextPrimary)
                Text(text = value, fontSize = 11.sp, color = VoidTextSecondary)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = null, tint = VoidTextDisabled, modifier = Modifier.size(12.dp))
    }
}
