package com.murzik.voidlauncher.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murzik.voidlauncher.R
import com.murzik.voidlauncher.ui.LauncherViewModel
import com.murzik.voidlauncher.ui.Screen
import com.murzik.voidlauncher.ui.theme.VoidBlack
import com.murzik.voidlauncher.ui.theme.VoidBorder
import com.murzik.voidlauncher.ui.theme.VoidTextPrimary
import com.murzik.voidlauncher.ui.theme.VoidTextSecondary

@Composable
fun HiddenAppsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
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
            IconButton(onClick = { viewModel.navigateTo(Screen.SETTINGS) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = VoidTextSecondary
                )
            }
            Text(
                text = stringResource(R.string.hidden_apps_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = VoidTextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        HorizontalDivider(color = VoidBorder)

        if (hiddenApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет скрытых приложений",
                    color = VoidTextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(hiddenApps, key = { it.id }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = app.name,
                                fontSize = 17.sp,
                                color = VoidTextPrimary
                            )
                            Text(
                                text = app.packageName,
                                fontSize = 12.sp,
                                color = VoidTextSecondary
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.toggleHide(app) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = VoidTextPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(text = stringResource(R.string.unhide_app), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
