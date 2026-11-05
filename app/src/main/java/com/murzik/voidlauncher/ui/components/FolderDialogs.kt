package com.murzik.voidlauncher.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.murzik.voidlauncher.model.AppFolder
import com.murzik.voidlauncher.model.AppInfo
import com.murzik.voidlauncher.ui.theme.ThemeMode
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidDanger
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass

// Folder item rendered on Home Screen
@Composable
fun FolderListItem(
    folder: AppFolder,
    appsCount: Int,
    textOnlyMode: Boolean,
    hapticEnabled: Boolean,
    themeMode: ThemeMode,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "FolderScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .liquidGlass(
                themeMode = themeMode,
                shape = RoundedCornerShape(16.dp),
                borderWidth = 1.dp,
                alpha = 0.10f
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = folder.emoji,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 14.dp)
            )
            Column {
                Text(
                    text = folder.name,
                    fontSize = 18.sp,
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

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "➔",
                fontSize = 13.sp,
                color = VoidTextSecondary
            )
        }
    }
}

// Full Liquid Glass Modal Expansion of a Folder
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderExpansionBottomSheet(
    folder: AppFolder,
    appsInFolder: List<AppInfo>,
    textOnlyMode: Boolean,
    hapticEnabled: Boolean,
    themeMode: ThemeMode,
    onDismiss: () -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    onEditFolder: () -> Unit,
    onDeleteFolder: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0A0A0A).copy(alpha = 0.96f),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = folder.emoji,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = folder.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = VoidTextPrimary
                        )
                        Text(
                            text = "${appsInFolder.size} прилож. внутри",
                            fontSize = 13.sp,
                            color = VoidTextSecondary
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEditFolder) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", tint = VoidTextSecondary)
                    }
                    IconButton(onClick = onDeleteFolder) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = VoidDanger)
                    }
                }
            }

            HorizontalDivider(color = VoidBorder, modifier = Modifier.padding(vertical = 8.dp))

            // Apps List inside Folder
            if (appsInFolder.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "В этой папке пока нет приложений\nНажмите карандаш сверху чтобы добавить",
                        color = VoidTextSecondary,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    items(appsInFolder, key = { it.id }) { app ->
                        AppListItem(
                            app = app,
                            textOnlyMode = textOnlyMode,
                            hapticEnabled = hapticEnabled,
                            onClick = { onLaunchApp(app) },
                            onLongClick = {}
                        )
                    }
                }
            }
        }
    }
}

// Dialog to Create or Edit Folder
@Composable
fun FolderCreateOrEditDialog(
    initialFolder: AppFolder? = null,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (AppFolder) -> Unit
) {
    var folderName by remember { mutableStateOf(initialFolder?.name ?: "") }
    var selectedEmoji by remember { mutableStateOf(initialFolder?.emoji ?: "📁") }
    val selectedAppIds = remember {
        mutableStateListOf<String>().apply {
            initialFolder?.appIds?.let { addAll(it) }
        }
    }

    val availableEmojis = listOf("📁", "🎮", "💬", "📸", "🎵", "💼", "⚡", "🚀", "🛠️", "🎬", "📱", "🌐", "🔒", "⭐")

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF141414))
                .border(1.dp, VoidBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = if (initialFolder == null) "Создать папку" else "Редактировать папку",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Folder Name Input
            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                label = { Text("Название папки") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = VoidTextPrimary,
                    unfocusedTextColor = VoidTextPrimary,
                    focusedBorderColor = VoidTextPrimary,
                    unfocusedBorderColor = VoidBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Emoji selector
            Text(text = "Иконка папки:", fontSize = 13.sp, color = VoidTextSecondary)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (selectedEmoji == emoji) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                            .border(if (selectedEmoji == emoji) 1.5.dp else 1.dp, if (selectedEmoji == emoji) VoidTextPrimary else VoidBorder, CircleShape)
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Select Apps Checklist
            Text(
                text = "Приложения в папке (${selectedAppIds.size}):",
                fontSize = 13.sp,
                color = VoidTextSecondary
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 6.dp)
            ) {
                items(allApps, key = { it.id }) { app ->
                    val isChecked = selectedAppIds.contains(app.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (isChecked) selectedAppIds.remove(app.id)
                                else selectedAppIds.add(app.id)
                            }
                            .padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = app.name, fontSize = 15.sp, color = VoidTextPrimary)
                        if (isChecked) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = null, tint = VoidTextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VoidTextSecondary)
                ) {
                    Text("Отмена")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        val finalName = if (folderName.isBlank()) "Папка" else folderName.trim()
                        val resultFolder = initialFolder?.copy(
                            name = finalName,
                            emoji = selectedEmoji,
                            appIds = selectedAppIds.toList()
                        ) ?: AppFolder(
                            name = finalName,
                            emoji = selectedEmoji,
                            appIds = selectedAppIds.toList()
                        )
                        onSave(resultFolder)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VoidTextPrimary, contentColor = Color.Black)
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}
