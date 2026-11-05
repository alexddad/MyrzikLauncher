package com.murzik.voidlauncher.model

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Immutable

@Immutable
data class AppInfo(
    val name: String,
    val packageName: String,
    val activityName: String,
    val customLabel: String? = null,
    val nameLower: String = (customLabel ?: name).lowercase(),
    val icon: Drawable? = null,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false
) {
    val id: String = "$packageName/$activityName"
    val displayName: String = customLabel ?: name
}
