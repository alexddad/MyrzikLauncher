package com.murzik.voidlauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "void_launcher_prefs")

enum class ClockStyle {
    MINIMAL,
    BOLD,
    STACKED
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
    val quickNotes: String = ""
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
    }

    val preferencesFlow: Flow<LauncherPreferences> = context.dataStore.data.map { prefs ->
        val styleStr = prefs[KEY_CLOCK_STYLE] ?: ClockStyle.MINIMAL.name
        val style = try { ClockStyle.valueOf(styleStr) } catch (_: Exception) { ClockStyle.MINIMAL }

        LauncherPreferences(
            isFirstLaunchCompleted = prefs[KEY_FIRST_LAUNCH] ?: false,
            pinnedAppIds = prefs[KEY_PINNED_APPS] ?: emptySet(),
            hiddenAppIds = prefs[KEY_HIDDEN_APPS] ?: emptySet(),
            textOnlyMode = prefs[KEY_TEXT_ONLY] ?: true,
            hapticFeedback = prefs[KEY_HAPTIC] ?: true,
            clockVisible = prefs[KEY_CLOCK_VISIBLE] ?: true,
            clock24Hour = prefs[KEY_CLOCK_24H] ?: true,
            clockStyle = style,
            quickNotes = prefs[KEY_QUICK_NOTES] ?: ""
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

    suspend fun saveQuickNotes(notes: String) {
        context.dataStore.edit { it[KEY_QUICK_NOTES] = notes }
    }
}
