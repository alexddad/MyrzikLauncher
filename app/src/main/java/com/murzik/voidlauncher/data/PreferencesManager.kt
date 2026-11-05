package com.murzik.voidlauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.murzik.voidlauncher.model.AppFolder
import com.murzik.voidlauncher.ui.theme.AccentColor
import com.murzik.voidlauncher.ui.theme.AppAlignment
import com.murzik.voidlauncher.ui.theme.TextSizeScale
import com.murzik.voidlauncher.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "void_launcher_prefs")

enum class ClockStyle(val title: String) {
    MINIMAL("Минимальный цифровой"),
    STACKED("Крупные в две строки"),
    TEXT_CLOCK("Словами (Текстовые)"),
    ANALOG_MINIMAL("Минималистичный циферблат")
}

data class LauncherPreferences(
    val isFirstLaunchCompleted: Boolean = false,
    val pinnedAppIds: Set<String> = emptySet(),
    val hiddenAppIds: Set<String> = emptySet(),
    val textOnlyMode: Boolean = true,
    val hapticFeedback: Boolean = true,
    val clockVisible: Boolean = true,
    val clock24Hour: Boolean = true,
    val clockStyle: ClockStyle = ClockStyle.MINIMAL,
    val quickNotes: String = "",
    val themeMode: ThemeMode = ThemeMode.LIQUID_GLASS,
    val accentColor: AccentColor = AccentColor.WHITE,
    val textSizeScale: TextSizeScale = TextSizeScale.STANDARD,
    val appAlignment: AppAlignment = AppAlignment.LEFT,
    val customGreeting: String = "Xiaomi 17T • Murzik OS",
    val folders: List<AppFolder> = emptyList(),
    val customAppNames: Map<String, String> = emptyMap(),
    val showScreenTime: Boolean = true
)

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch_completed")
        private val KEY_PINNED_APPS = stringSetPreferencesKey("pinned_apps")
        private val KEY_HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")
        private val KEY_TEXT_ONLY = booleanPreferencesKey("text_only_mode")
        private val KEY_HAPTIC = booleanPreferencesKey("haptic_feedback")
        private val KEY_CLOCK_VISIBLE = booleanPreferencesKey("clock_visible")
        private val KEY_CLOCK_24H = booleanPreferencesKey("clock_24h")
        private val KEY_CLOCK_STYLE = stringPreferencesKey("clock_style")
        private val KEY_QUICK_NOTES = stringPreferencesKey("quick_notes")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_ACCENT_COLOR = stringPreferencesKey("accent_color")
        private val KEY_TEXT_SIZE = stringPreferencesKey("text_size_scale")
        private val KEY_APP_ALIGNMENT = stringPreferencesKey("app_alignment")
        private val KEY_CUSTOM_GREETING = stringPreferencesKey("custom_greeting")
        private val KEY_FOLDERS_JSON = stringPreferencesKey("folders_json")
        private val KEY_CUSTOM_NAMES = stringPreferencesKey("custom_names_json")
        private val KEY_SHOW_SCREEN_TIME = booleanPreferencesKey("show_screen_time")
    }

    val preferencesFlow: Flow<LauncherPreferences> = context.dataStore.data.map { prefs ->
        val styleStr = prefs[KEY_CLOCK_STYLE] ?: ClockStyle.MINIMAL.name
        val style = try { ClockStyle.valueOf(styleStr) } catch (_: Exception) { ClockStyle.MINIMAL }

        val themeStr = prefs[KEY_THEME_MODE] ?: ThemeMode.LIQUID_GLASS.name
        val theme = try { ThemeMode.valueOf(themeStr) } catch (_: Exception) { ThemeMode.LIQUID_GLASS }

        val accentStr = prefs[KEY_ACCENT_COLOR] ?: AccentColor.WHITE.name
        val accent = try { AccentColor.valueOf(accentStr) } catch (_: Exception) { AccentColor.WHITE }

        val sizeStr = prefs[KEY_TEXT_SIZE] ?: TextSizeScale.STANDARD.name
        val sizeScale = try { TextSizeScale.valueOf(sizeStr) } catch (_: Exception) { TextSizeScale.STANDARD }

        val alignStr = prefs[KEY_APP_ALIGNMENT] ?: AppAlignment.LEFT.name
        val align = try { AppAlignment.valueOf(alignStr) } catch (_: Exception) { AppAlignment.LEFT }

        val foldersJsonStr = prefs[KEY_FOLDERS_JSON] ?: "[]"
        val parsedFolders = mutableListOf<AppFolder>()
        try {
            val jsonArr = JSONArray(foldersJsonStr)
            for (i in 0 until jsonArr.length()) {
                val folderObj = jsonArr.getString(i)
                AppFolder.fromJson(folderObj)?.let { parsedFolders.add(it) }
            }
        } catch (_: Exception) {}

        val customNamesMap = mutableMapOf<String, String>()
        try {
            val namesJson = org.json.JSONObject(prefs[KEY_CUSTOM_NAMES] ?: "{}")
            val keys = namesJson.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                customNamesMap[k] = namesJson.getString(k)
            }
        } catch (_: Exception) {}

        LauncherPreferences(
            isFirstLaunchCompleted = prefs[KEY_FIRST_LAUNCH] ?: false,
            pinnedAppIds = prefs[KEY_PINNED_APPS] ?: emptySet(),
            hiddenAppIds = prefs[KEY_HIDDEN_APPS] ?: emptySet(),
            textOnlyMode = prefs[KEY_TEXT_ONLY] ?: true,
            hapticFeedback = prefs[KEY_HAPTIC] ?: true,
            clockVisible = prefs[KEY_CLOCK_VISIBLE] ?: true,
            clock24Hour = prefs[KEY_CLOCK_24H] ?: true,
            clockStyle = style,
            quickNotes = prefs[KEY_QUICK_NOTES] ?: "",
            themeMode = theme,
            accentColor = accent,
            textSizeScale = sizeScale,
            appAlignment = align,
            customGreeting = prefs[KEY_CUSTOM_GREETING] ?: "Xiaomi 17T • Murzik OS",
            folders = parsedFolders,
            customAppNames = customNamesMap,
            showScreenTime = prefs[KEY_SHOW_SCREEN_TIME] ?: true
        )
    }

    suspend fun setFirstLaunchCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_FIRST_LAUNCH] = completed }
    }

    suspend fun togglePinApp(appId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_PINNED_APPS]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(appId)) {
                current.remove(appId)
            } else {
                current.add(appId)
            }
            prefs[KEY_PINNED_APPS] = current
        }
    }

    suspend fun toggleHideApp(appId: String) {
        context.dataStore.edit { prefs ->
            val currentHidden = prefs[KEY_HIDDEN_APPS]?.toMutableSet() ?: mutableSetOf()
            val currentPinned = prefs[KEY_PINNED_APPS]?.toMutableSet() ?: mutableSetOf()
            if (currentHidden.contains(appId)) {
                currentHidden.remove(appId)
            } else {
                currentHidden.add(appId)
                currentPinned.remove(appId)
                prefs[KEY_PINNED_APPS] = currentPinned
            }
            prefs[KEY_HIDDEN_APPS] = currentHidden
        }
    }

    suspend fun setTextOnlyMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_TEXT_ONLY] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTIC] = enabled }
    }

    suspend fun setClockVisible(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CLOCK_VISIBLE] = enabled }
    }

    suspend fun setClock24Hour(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CLOCK_24H] = enabled }
    }

    suspend fun setClockStyle(style: ClockStyle) {
        context.dataStore.edit { it[KEY_CLOCK_STYLE] = style.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setAccentColor(accent: AccentColor) {
        context.dataStore.edit { it[KEY_ACCENT_COLOR] = accent.name }
    }

    suspend fun setTextSizeScale(scale: TextSizeScale) {
        context.dataStore.edit { it[KEY_TEXT_SIZE] = scale.name }
    }

    suspend fun setAppAlignment(alignment: AppAlignment) {
        context.dataStore.edit { it[KEY_APP_ALIGNMENT] = alignment.name }
    }

    suspend fun setCustomGreeting(greeting: String) {
        context.dataStore.edit { it[KEY_CUSTOM_GREETING] = greeting }
    }

    suspend fun saveQuickNotes(notes: String) {
        context.dataStore.edit { it[KEY_QUICK_NOTES] = notes }
    }

    suspend fun setCustomAppName(appId: String, newName: String) {
        context.dataStore.edit { prefs ->
            val json = try { org.json.JSONObject(prefs[KEY_CUSTOM_NAMES] ?: "{}") } catch (_: Exception) { org.json.JSONObject() }
            if (newName.isBlank()) {
                json.remove(appId)
            } else {
                json.put(appId, newName.trim())
            }
            prefs[KEY_CUSTOM_NAMES] = json.toString()
        }
    }

    suspend fun setShowScreenTime(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_SCREEN_TIME] = show }
    }

    // Folders Management
    suspend fun saveFolder(folder: AppFolder) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEY_FOLDERS_JSON] ?: "[]"
            val list = parseFolders(jsonStr).toMutableList()
            val existingIndex = list.indexOfFirst { it.id == folder.id }
            if (existingIndex >= 0) {
                list[existingIndex] = folder
            } else {
                list.add(folder)
            }
            prefs[KEY_FOLDERS_JSON] = serializeFolders(list)
        }
    }

    suspend fun deleteFolder(folderId: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEY_FOLDERS_JSON] ?: "[]"
            val list = parseFolders(jsonStr).filter { it.id != folderId }
            prefs[KEY_FOLDERS_JSON] = serializeFolders(list)
        }
    }

    suspend fun addAppToFolder(folderId: String, appId: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEY_FOLDERS_JSON] ?: "[]"
            val list = parseFolders(jsonStr).map { f ->
                if (f.id == folderId && !f.appIds.contains(appId)) {
                    f.copy(appIds = f.appIds + appId)
                } else f
            }
            prefs[KEY_FOLDERS_JSON] = serializeFolders(list)
        }
    }

    suspend fun removeAppFromFolder(folderId: String, appId: String) {
        context.dataStore.edit { prefs ->
            val jsonStr = prefs[KEY_FOLDERS_JSON] ?: "[]"
            val list = parseFolders(jsonStr).map { f ->
                if (f.id == folderId) {
                    f.copy(appIds = f.appIds.filter { it != appId })
                } else f
            }
            prefs[KEY_FOLDERS_JSON] = serializeFolders(list)
        }
    }

    private fun parseFolders(jsonStr: String): List<AppFolder> {
        val list = mutableListOf<AppFolder>()
        try {
            val jsonArr = JSONArray(jsonStr)
            for (i in 0 until jsonArr.length()) {
                AppFolder.fromJson(jsonArr.getString(i))?.let { list.add(it) }
            }
        } catch (_: Exception) {}
        return list
    }

    private fun serializeFolders(folders: List<AppFolder>): String {
        val jsonArr = JSONArray()
        folders.forEach { jsonArr.put(it.toJson()) }
        return jsonArr.toString()
    }
}
