package com.murzik.voidlauncher.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidCardSurface
import com.murzik.voidlauncher.ui.theme.VoidTextDisabled
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary

@Composable
fun OnboardingScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }
    val preferences by viewModel.preferences.collectAsState()
    val visibleApps by viewModel.visibleApps.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step indicator dots
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..4) {
                Box(
                    modifier = Modifier
                        .size(width = if (i == step) 24.dp else 8.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(if (i == step) VoidTextPrimary else VoidBorder)
                )
            }
        }

        // Animated step content
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                fadeIn(animationSpec = tween(120)) togetherWith fadeOut(animationSpec = tween(120))
            },
            label = "OnboardingTransition",
            modifier = Modifier.weight(1f)
        ) { currentStep ->
            when (currentStep) {
                1 -> StepWelcome()
                2 -> StepAppearance(
                    textOnly = preferences.textOnlyMode,
                    onToggleTextOnly = { viewModel.setTextOnlyMode(it) },
                    haptic = preferences.hapticFeedback,
                    onToggleHaptic = { viewModel.setHapticFeedback(it) }
                )
                3 -> StepSelectFavorites(
                    apps = visibleApps,
                    onTogglePin = { viewModel.togglePin(it) }
                )
                4 -> StepFinish(
                    onSetDefault = { viewModel.appRepository.openDefaultLauncherSettings() }
                )
            }
        }

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step-- },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VoidTextSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← Назад")
                }
            } else {
                Spacer(modifier = Modifier.size(1.dp))
            }

            if (step < 4) {
                Button(
                    onClick = { step++ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoidTextPrimary,
                        contentColor = VoidBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (step == 1) "Начать настройку →" else "Далее →")
                }
            } else {
                Button(
                    onClick = { viewModel.completeOnboarding() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoidTextPrimary,
                        contentColor = VoidBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(text = "Запустить Murzik OS", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun StepWelcome() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Minimalist Void Symbol
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(3.dp, VoidTextPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(VoidTextPrimary)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Добро пожаловать в\nMurzik OS",
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-0.5).sp,
            textAlign = TextAlign.Center,
            color = VoidTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Простая и быстрая системная оболочка для вашего Xiaomi 17T.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            color = VoidTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Высокая производительность • Экономия заряда • Ничего лишнего",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = VoidTextDisabled
        )
    }
}

@Composable
private fun StepAppearance(
    textOnly: Boolean,
    onToggleTextOnly: (Boolean) -> Unit,
    haptic: Boolean,
    onToggleHaptic: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Выберите стиль интерфейса",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = VoidTextPrimary
        )
        Text(
            text = "Вы сможете изменить это в любой момент в настройках.",
            fontSize = 14.sp,
            color = VoidTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Option 1: Pure Void (Text Only)
        ModeCard(
            title = "Чистый текст (Pure Void)",
            description = "Никаких ярких картинок и отвлекающих факторов. Только элегантная типографика и максимальная автономность.",
            isSelected = textOnly,
            onClick = { onToggleTextOnly(true) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Icons
        ModeCard(
            title = "С аккуратными иконками",
            description = "Привычный вид с отображением значков приложений рядом с текстом.",
            isSelected = !textOnly,
            onClick = { onToggleTextOnly(false) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Haptic feedback toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VoidCardSurface)
                .clickable { onToggleHaptic(!haptic) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Тактильный отклик (вибрация)", fontSize = 15.sp, color = VoidTextPrimary)
                Text(text = "Легкая отдача при нажатии", fontSize = 12.sp, color = VoidTextSecondary)
            }
            Icon(
                imageVector = if (haptic) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (haptic) VoidTextPrimary else VoidTextDisabled
            )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VoidCardSurface)
            .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) VoidTextPrimary else VoidBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = VoidTextPrimary)
            Text(text = description, fontSize = 13.sp, color = VoidTextSecondary, modifier = Modifier.padding(top = 4.dp))
        }
        Icon(
            imageVector = if (isSelected) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isSelected) VoidTextPrimary else VoidTextDisabled,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun StepSelectFavorites(
    apps: List<com.murzik.voidlauncher.model.AppInfo>,
    onTogglePin: (com.murzik.voidlauncher.model.AppInfo) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Выберите избранные приложения",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = VoidTextPrimary
        )
        Text(
            text = "Они будут всегда под рукой на главном экране (отметьте 4–7 приложений):",
            fontSize = 14.sp,
            color = VoidTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            items(apps, key = { it.id }) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTogglePin(app) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = app.name,
                        fontSize = 16.sp,
                        color = if (app.isPinned) VoidTextPrimary else VoidTextSecondary,
                        fontWeight = if (app.isPinned) FontWeight.Medium else FontWeight.Normal
                    )
                    if (app.isPinned) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = VoidTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepFinish(
    onSetDefault: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(68.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Всё готово к работе!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            color = VoidTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Чтобы телефон не возвращался к стандартному экрану Xiaomi при нажатии Home, сделайте Murzik OS лаунчером по умолчанию.",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            color = VoidTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedButton(
            onClick = onSetDefault,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = "Сделать лаунчером по умолчанию", modifier = Modifier.padding(start = 10.dp), fontSize = 14.sp)
        }
    }
}
