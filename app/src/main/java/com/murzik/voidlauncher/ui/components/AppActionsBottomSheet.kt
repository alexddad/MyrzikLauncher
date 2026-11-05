package com.murzik.voidlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.murzik.voidlauncher.R
import com.murzik.voidlauncher.model.AppInfo
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidDanger
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppActionsBottomSheet(
    app: AppInfo,
    onDismiss: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleHide: () -> Unit,
    onRename: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = VoidCardSurface,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            // Header with App Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = app.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VoidTextPrimary
                )
                Text(
                    text = if (app.customLabel != null) "Исходное: ${app.name} • ${app.packageName}" else app.packageName,
                    fontSize = 12.sp,
                    color = VoidTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = VoidBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Pin / Unpin
            ActionRow(
                icon = Icons.Outlined.PushPin,
                title = if (app.isPinned) stringResource(R.string.unpin_app) else stringResource(R.string.pin_app),
                onClick = onTogglePin
            )

            // Rename (Void Launcher signature feature)
            ActionRow(
                icon = Icons.Outlined.Edit,
                title = "Переименовать приложение",
                onClick = onRename
            )

            // Hide
            ActionRow(
                icon = Icons.Outlined.VisibilityOff,
                title = if (app.isHidden) stringResource(R.string.unhide_app) else stringResource(R.string.hide_app),
                onClick = onToggleHide
            )

            // System App Info
            ActionRow(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.app_info),
                onClick = onAppInfo
            )

            // Uninstall
            ActionRow(
                icon = Icons.Outlined.Delete,
                title = stringResource(R.string.uninstall_app),
                isDanger = true,
                onClick = onUninstall
            )
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (isDanger) VoidDanger else VoidTextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = contentColor
        )
    }
}

@Composable
fun AppRenameDialog(
    app: AppInfo,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(app.customLabel ?: app.name) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF141414))
                .border(1.dp, VoidBorder, RoundedCornerShape(20.dp))
                .padding(22.dp)
        ) {
            Text(
                text = "Переименовать",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextPrimary
            )
            Text(
                text = "Исходное: ${app.name}",
                fontSize = 12.sp,
                color = VoidTextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Новое имя") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = VoidTextPrimary,
                    unfocusedTextColor = VoidTextPrimary,
                    focusedBorderColor = VoidTextPrimary,
                    unfocusedBorderColor = VoidBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

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
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { onSave(newName) },
                    colors = ButtonDefaults.buttonColors(containerColor = VoidTextPrimary, contentColor = Color.Black)
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}
