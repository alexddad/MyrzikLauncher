package com.murzik.voidlauncher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(
    is24Hour: Boolean,
    onClockClick: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    // Optimized battery-friendly ticker: only wakes up once per minute at the exact minute mark
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val millisUntilNextMinute = 60000L - (now % 60000L) + 20L
            delay(millisUntilNextMinute)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    val currentDate = remember(currentTimeMillis) { Date(currentTimeMillis) }

    val timeFormat = remember(is24Hour) {
        if (is24Hour) SimpleDateFormat("HH:mm", Locale.getDefault())
        else SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    val dateFormat = remember {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    }

    val timeString = remember(currentDate, timeFormat) { timeFormat.format(currentDate) }
    val dateString = remember(currentDate, dateFormat) { dateFormat.format(currentDate) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = timeString,
            fontSize = 58.sp,
            fontWeight = FontWeight.ExtraLight,
            letterSpacing = (-1).sp,
            color = VoidTextPrimary,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClockClick
            )
        )

        Text(
            text = dateString.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            fontSize = 16.sp,
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
    }
}
