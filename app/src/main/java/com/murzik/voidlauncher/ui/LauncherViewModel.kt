package com.murzik.voidlauncher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.murzik.voidlauncher.data.AppRepository
import com.murzik.voidlauncher.data.LauncherPreferences
import com.murzik.voidlauncher.data.PreferencesManager
import com.murzik.voidlauncher.model.AppInfo
import com.murzik.voidlauncher.receiver.PackageChangeReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    DRAWER,
    SETTINGS,
    HIDDEN_APPS
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    val appRepository = AppRepository(application)

    val preferences: StateFlow<LauncherPreferences> = preferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LauncherPreferences()
        )

    private val _rawInstalledApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen

    private val _selectedAppForMenu = MutableStateFlow<AppInfo?>(null)
    val selectedAppForMenu: StateFlow<AppInfo?> = _selectedAppForMenu

    // Pinned apps for Home screen (excluding hidden)
    val pinnedApps: StateFlow<List<AppInfo>> = combine(_rawInstalledApps, preferences) { apps, _ ->
        apps.filter { it.isPinned && !it.isHidden }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All visible apps for App Drawer (excluding hidden)
    val visibleApps: StateFlow<List<AppInfo>> = combine(_rawInstalledApps, preferences) { apps, _ ->
        apps.filter { !it.isHidden }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Hidden apps
    val hiddenApps: StateFlow<List<AppInfo>> = combine(_rawInstalledApps, preferences) { apps, _ ->
        apps.filter { it.isHidden }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // High-performance search filtering: prefix matches first, then substring
    val filteredApps: StateFlow<List<AppInfo>> = combine(visibleApps, _searchQuery) { apps, query ->
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            apps
        } else {
            val prefixMatches = ArrayList<AppInfo>()
            val containsMatches = ArrayList<AppInfo>()

            for (app in apps) {
                if (app.nameLower.startsWith(trimmed)) {
                    prefixMatches.add(app)
                } else if (app.nameLower.contains(trimmed) || app.packageName.contains(trimmed)) {
                    containsMatches.add(app)
                }
            }
            prefixMatches.apply { addAll(containsMatches) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadApps()
        PackageChangeReceiver.onPackageChangedListener = {
            loadApps()
        }
        viewModelScope.launch {
            preferences.collect {
                loadApps()
            }
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            val currentPrefs = preferences.value
            val list = appRepository.loadInstalledApps(
                pinnedIds = currentPrefs.pinnedAppIds,
                hiddenIds = currentPrefs.hiddenAppIds,
                loadIcons = !currentPrefs.textOnlyMode
            )
            _rawInstalledApps.value = list
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen == Screen.DRAWER) {
            _searchQuery.value = ""
        }
    }

    fun openAppMenu(app: AppInfo) {
        _selectedAppForMenu.value = app
    }

    fun closeAppMenu() {
        _selectedAppForMenu.value = null
    }

    fun togglePin(app: AppInfo) {
        viewModelScope.launch {
            preferencesManager.togglePinApp(app.id)
            closeAppMenu()
        }
    }

    fun toggleHide(app: AppInfo) {
        viewModelScope.launch {
            preferencesManager.toggleHideApp(app.id)
            closeAppMenu()
        }
    }

    fun launchApp(app: AppInfo) {
        appRepository.launchApp(app)
        _searchQuery.value = ""
        _currentScreen.value = Screen.HOME
    }

    fun setTextOnlyMode(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setTextOnlyMode(enabled) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setHapticFeedback(enabled) }
    }

    fun setClockVisible(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setClockVisible(enabled) }
    }

    fun setClock24Hour(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setClock24Hour(enabled) }
    }

    fun handleBackPressed(): Boolean {
        return when (_currentScreen.value) {
            Screen.DRAWER, Screen.SETTINGS, Screen.HIDDEN_APPS -> {
                _currentScreen.value = Screen.HOME
                _searchQuery.value = ""
                true
            }
            Screen.HOME -> false
        }
    }

    override fun onCleared() {
        super.onCleared()
        PackageChangeReceiver.onPackageChangedListener = null
    }
}
