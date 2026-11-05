package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import androidx.compose.foundation.shape.RoundedCornerShape
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import com.murzik.voidlauncher.ui.theme.liquidGlass

@Composable
fun QuickNotesScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()
    var noteText by remember(preferences.quickNotes) { mutableStateOf(preferences.quickNotes) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
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
                text = "Быстрые Заметки Murzik OS",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextPrimary,
                modifier = Modifier.padding(start = 8.dp).weight(1f)
            )
            Text(
                text = "${noteText.length} симв.",
                fontSize = 13.sp,
                color = VoidTextSecondary,
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        HorizontalDivider(color = VoidBorder)

        // Notes content editor in Liquid Glass canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .liquidGlass(
                    themeMode = preferences.themeMode,
                    shape = RoundedCornerShape(22.dp),
                    alpha = 0.14f
                )
                .padding(20.dp)
        ) {
            if (noteText.isEmpty()) {
                Text(
                    text = "Запишите мысли, список покупок или дела…\n(Текст сохраняется автоматически)",
                    color = VoidTextSecondary,
                    fontSize = 17.sp,
                    lineHeight = 26.sp
                )
            }
            BasicTextField(
                value = noteText,
                onValueChange = {
                    noteText = it
                    viewModel.saveQuickNotes(it)
                },
                textStyle = TextStyle(
                    color = VoidTextPrimary,
                    fontSize = 17.sp,
                    lineHeight = 26.sp
                ),
                cursorBrush = SolidColor(VoidTextPrimary),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
