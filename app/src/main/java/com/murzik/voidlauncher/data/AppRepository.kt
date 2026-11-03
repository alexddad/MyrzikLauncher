package com.murzik.voidlauncher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import com.murzik.voidlauncher.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Method

class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    suspend fun loadInstalledApps(
        pinnedIds: Set<String>,
        hiddenIds: Set<String>,
        loadIcons: Boolean = false
    ): List<AppInfo> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0)
        val selfPackage = context.packageName

        resolveInfos
            .asSequence()
            .filter { it.activityInfo.packageName != selfPackage }
            .map { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                val activityName = resolveInfo.activityInfo.name
                val id = "$packageName/$activityName"
                val name = resolveInfo.loadLabel(packageManager).toString()
                val icon = if (loadIcons) resolveInfo.loadIcon(packageManager) else null

                AppInfo(
                    name = name,
                    packageName = packageName,
                    activityName = activityName,
                    nameLower = name.lowercase(),
                    icon = icon,
                    isPinned = pinnedIds.contains(id),
                    isHidden = hiddenIds.contains(id)
                )
            }
            .sortedBy { it.nameLower }
            .toList()
    }

    fun launchApp(app: AppInfo) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, app.activityName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallbackIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            fallbackIntent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        }
    }

    fun openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openDefaultLauncherSettings() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        }
    }

    fun openClockApp() {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Clock app fallback
        }
    }

    fun openCalendarApp() {
        try {
            val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
            val intent = Intent(Intent.ACTION_VIEW, builder.build()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Calendar fallback
        }
    }

    fun expandNotificationShade() {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManagerClass = Class.forName("android.app.StatusBarManager")
            val expandNotifications: Method = statusBarManagerClass.getMethod("expandNotificationsPanel")
            expandNotifications.invoke(statusBarService)
        } catch (_: Exception) {
            // Status bar fallback
        }
    }
}
