package com.murzik.voidlauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "void_launcher_prefs")

data class LauncherPreferences(
    val pinnedAppIds: Set<String> = emptySet(),
    val hiddenAppIds: Set<String> = emptySet(),
    val textOnlyMode: Boolean = true,
    val hapticFeedback: Boolean = true,
    val clockVisible: Boolean = true,
    val clock24Hour: Boolean = true
)

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_PINNED_APPS = stringSetPreferencesKey("pinned_apps")
        private val KEY_HIDDEN_APPS = stringSetPreferencesKey("hidden_apps")
        private val KEY_TEXT_ONLY = booleanPreferencesKey("text_only_mode")
        private val KEY_HAPTIC = booleanPreferencesKey("haptic_feedback")
        private val KEY_CLOCK_VISIBLE = booleanPreferencesKey("clock_visible")
        private val KEY_CLOCK_24H = booleanPreferencesKey("clock_24h")
    }

    val preferencesFlow: Flow<LauncherPreferences> = context.dataStore.data.map { prefs ->
        LauncherPreferences(
            pinnedAppIds = prefs[KEY_PINNED_APPS] ?: emptySet(),
            hiddenAppIds = prefs[KEY_HIDDEN_APPS] ?: emptySet(),
            textOnlyMode = prefs[KEY_TEXT_ONLY] ?: true,
            hapticFeedback = prefs[KEY_HAPTIC] ?: true,
            clockVisible = prefs[KEY_CLOCK_VISIBLE] ?: true,
            clock24Hour = prefs[KEY_CLOCK_24H] ?: true
        )
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
                // If an app is hidden, unpin it
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
}
