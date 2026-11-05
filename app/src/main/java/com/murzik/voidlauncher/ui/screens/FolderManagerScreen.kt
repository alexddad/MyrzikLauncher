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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidDanger
import com.murzik.voidlauncher.ui.theme.VoidDarkSurface
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass

@Composable
fun FolderManagerScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    val folders by viewModel.folders.collectAsState()

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(Screen.SETTINGS) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = VoidTextSecondary
                    )
                }
                Text(
                    text = "Управление папками",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VoidTextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            IconButton(onClick = { viewModel.startCreateFolder() }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add Folder",
                    tint = VoidTextPrimary
                )
            }
        }

        HorizontalDivider(color = VoidBorder)

        if (folders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📁",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "У вас пока нет папок",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = VoidTextPrimary
                    )
                    Text(
                        text = "Создавайте любые папки: «Игры», «Соцсети», «Работа», «Финансы» и добавляйте в них сколько угодно приложений.",
                        fontSize = 13.sp,
                        color = VoidTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                    )
                    Button(
                        onClick = { viewModel.startCreateFolder() },
                        colors = ButtonDefaults.buttonColors(containerColor = VoidTextPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Создать первую папку")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(folders, key = { it.id }) { folder ->
                    val appsCount = viewModel.getAppsForFolder(folder).size

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                themeMode = preferences.themeMode,
                                shape = RoundedCornerShape(18.dp),
                                alpha = 0.12f
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = folder.emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = folder.name,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VoidTextPrimary
                                    )
                                    Text(
                                        text = "$appsCount прилож.",
                                        fontSize = 12.sp,
                                        color = VoidTextSecondary
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = { viewModel.startEditFolder(folder) }) {
                                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", tint = VoidTextSecondary, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { viewModel.deleteFolder(folder.id) }) {
                                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = VoidDanger, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                        // Toggle Pin to Home
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Показывать на Главном экране",
                                fontSize = 13.sp,
                                color = VoidTextSecondary
                            )
                            Switch(
                                checked = folder.isPinnedToHome,
                                onCheckedChange = { isPinned ->
                                    viewModel.saveFolder(folder.copy(isPinnedToHome = isPinned))
                                },
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
                }
            }
        }
    }
}
