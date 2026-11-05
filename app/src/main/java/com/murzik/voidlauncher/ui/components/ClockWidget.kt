package com.murzik.voidlauncher.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.data.ClockStyle
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockWidget(
    is24Hour: Boolean,
    clockStyle: ClockStyle,
    customGreeting: String,
    showScreenTime: Boolean = true,
    onClockClick: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(clockStyle) {
        val delayInterval = if (clockStyle == ClockStyle.ANALOG_MINIMAL) 1000L else 30000L
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(delayInterval)
        }
    }

    val currentDate = remember(currentTimeMillis) { Date(currentTimeMillis) }

    val hourFormat = remember(is24Hour) {
        if (is24Hour) SimpleDateFormat("HH", Locale.getDefault()) else SimpleDateFormat("hh", Locale.getDefault())
    }
    val minFormat = remember { SimpleDateFormat("mm", Locale.getDefault()) }
    val fullTimeFormat = remember(is24Hour) {
        if (is24Hour) SimpleDateFormat("HH:mm", Locale.getDefault()) else SimpleDateFormat("hh:mm a", Locale.getDefault())
    }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    val hourStr = remember(currentDate) { hourFormat.format(currentDate) }
    val minStr = remember(currentDate) { minFormat.format(currentDate) }
    val timeStr = remember(currentDate) { fullTimeFormat.format(currentDate) }
    val dateStr = remember(currentDate) { dateFormat.format(currentDate) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        // Custom Greeting pill
        if (customGreeting.isNotBlank()) {
            Text(
                text = customGreeting,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = VoidTextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // Clock Display based on Style
        when (clockStyle) {
            ClockStyle.MINIMAL -> {
                Text(
                    text = timeStr,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = (-1.5).sp,
                    color = VoidTextPrimary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClockClick
                    )
                )
            }
            ClockStyle.STACKED -> {
                Column(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClockClick
                    )
                ) {
                    Text(
                        text = hourStr,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 50.sp,
                        color = VoidTextPrimary
                    )
                    Text(
                        text = minStr,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 50.sp,
                        color = VoidTextPrimary
                    )
                }
            }
            ClockStyle.TEXT_CLOCK -> {
                Text(
                    text = "$hourStr : $minStr",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = 4.sp,
                    color = VoidTextPrimary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClockClick
                    )
                )
            }
            ClockStyle.ANALOG_MINIMAL -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClockClick
                        )
                        .padding(vertical = 4.dp)
                ) {
                    MinimalAnalogClock(currentTimeMillis = currentTimeMillis)
                    Text(
                        text = timeStr,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = VoidTextPrimary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // Date Line
        Text(
            text = dateStr.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = VoidTextSecondary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDateClick
                )
        )

        // Screen Time / Digital Wellbeing (Void signature feature)
        if (showScreenTime) {
            Text(
                text = "Экран сегодня: 1 ч. 42 мин. • Фокус активен",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MinimalAnalogClock(
    currentTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    val cal = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }
    val hours = cal.get(Calendar.HOUR)
    val minutes = cal.get(Calendar.MINUTE)
    val seconds = cal.get(Calendar.SECOND)

    Canvas(modifier = modifier.size(64.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Outer dial ring
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius - 2f,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )

        // 12 hour tick marks
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30).toDouble())
            val startX = center.x + (radius - 8f) * sin(angle).toFloat()
            val startY = center.y - (radius - 8f) * cos(angle).toFloat()
            val endX = center.x + (radius - 3f) * sin(angle).toFloat()
            val endY = center.y - (radius - 3f) * cos(angle).toFloat()
            drawLine(
                color = Color.White.copy(alpha = if (i % 3 == 0) 0.8f else 0.35f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (i % 3 == 0) 2.5f else 1.5f,
                cap = StrokeCap.Round
            )
        }

        // Hour hand
        val hourAngle = Math.toRadians(((hours % 12 + minutes / 60f) * 30).toDouble())
        val hourEnd = Offset(
            center.x + (radius * 0.5f) * sin(hourAngle).toFloat(),
            center.y - (radius * 0.5f) * cos(hourAngle).toFloat()
        )
        drawLine(
            color = Color.White,
            start = center,
            end = hourEnd,
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )

        // Minute hand
        val minAngle = Math.toRadians(((minutes + seconds / 60f) * 6).toDouble())
        val minEnd = Offset(
            center.x + (radius * 0.75f) * sin(minAngle).toFloat(),
            center.y - (radius * 0.75f) * cos(minAngle).toFloat()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = center,
            end = minEnd,
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )

        // Center dot
        drawCircle(
            color = Color.White,
            radius = 3.5f,
            center = center
        )
    }
}
