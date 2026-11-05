package com.murzik.voidlauncher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ThemeMode {
    LIQUID_GLASS,    // Натуральное матовое стекло (физическое преломление)
    DARK_GLASS,      // Тёмное полупрозрачное стекло
    AMOLED_BLACK,    // Чистый чёрный цвет без эффектов
    GRAPHITE         // Нейтральный матовый титан
}

enum class AccentColor(val title: String, val color: Color) {
    WHITE("Белый", Color(0xFFFFFFFF)),
    SLATE("Графит", Color(0xFF94A3B8)),
    BLUE("Серый синий", Color(0xFF38BDF8)),
    EMERALD("Спокойный зелёный", Color(0xFF34D399)),
    AMBER("Тёплый песочный", Color(0xFFFBBF24))
}

enum class TextSizeScale(val title: String, val multiplier: Float) {
    COMPACT("Компактный", 0.88f),
    STANDARD("Стандартный", 1.0f),
    LARGE("Крупный", 1.15f)
}

enum class AppAlignment(val title: String) {
    LEFT("По левому краю"),
    CENTER("По центру"),
    RIGHT("По правому краю")
}

object GlassEffects {

    fun liquidGlassBackground(
        themeMode: ThemeMode,
        alpha: Float = 0.12f
    ): Brush {
        return when (themeMode) {
            ThemeMode.LIQUID_GLASS -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF262626).copy(alpha = alpha + 0.10f),
                    Color(0xFF141414).copy(alpha = alpha * 0.6f),
                    Color(0xFF0F0F0F).copy(alpha = 0.75f)
                )
            )
            ThemeMode.DARK_GLASS -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF222222).copy(alpha = 0.45f),
                    Color(0xFF121212).copy(alpha = 0.75f)
                )
            )
            ThemeMode.GRAPHITE -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2A2A2A).copy(alpha = 0.60f),
                    Color(0xFF1A1A1A).copy(alpha = 0.90f)
                )
            )
            ThemeMode.AMOLED_BLACK -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF141414),
                    Color(0xFF101010)
                )
            )
        }
    }

    fun liquidGlassBorder(
        themeMode: ThemeMode,
        accentColor: Color = Color.White
    ): Brush {
        return when (themeMode) {
            ThemeMode.LIQUID_GLASS -> Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.30f),
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.03f)
                )
            )
            ThemeMode.DARK_GLASS -> Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.20f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
            ThemeMode.GRAPHITE -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4A4A4A),
                    Color(0xFF2A2A2A)
                )
            )
            ThemeMode.AMOLED_BLACK -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF262626),
                    Color(0xFF1C1C1C)
                )
            )
        }
    }
}

fun Modifier.liquidGlass(
    themeMode: ThemeMode = ThemeMode.LIQUID_GLASS,
    shape: Shape = RoundedCornerShape(20.dp),
    borderWidth: Dp = 1.dp,
    alpha: Float = 0.12f,
    accentColor: Color = Color.White
): Modifier = this
    .clip(shape)
    .background(GlassEffects.liquidGlassBackground(themeMode, alpha), shape)
    .border(borderWidth, GlassEffects.liquidGlassBorder(themeMode, accentColor), shape)
