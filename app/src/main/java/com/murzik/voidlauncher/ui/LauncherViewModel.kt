package com.murzik.voidlauncher.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.murzik.voidlauncher.data.AppRepository
import com.murzik.voidlauncher.data.ClockStyle
import com.murzik.voidlauncher.data.LauncherPreferences
import com.murzik.voidlauncher.data.PreferencesManager
import com.murzik.voidlauncher.data.SystemManager
import com.murzik.voidlauncher.model.AppFolder
import com.murzik.voidlauncher.model.AppInfo
import com.murzik.voidlauncher.receiver.PackageChangeReceiver
import com.murzik.voidlauncher.ui.theme.AccentColor
import com.murzik.voidlauncher.ui.theme.AppAlignment
import com.murzik.voidlauncher.ui.theme.TextSizeScale
import com.murzik.voidlauncher.ui.theme.ThemeMode
import com.murzik.voidlauncher.util.MathEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    DRAWER,
    SETTINGS,
    HIDDEN_APPS,
    CONTROL_CENTER,
    QUICK_NOTES,
    ONBOARDING,
    FOLDER_MANAGER,
    CALCULATOR,
    STORAGE_MANAGER
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    val appRepository = AppRepository(application)
    val systemManager = SystemManager(application)

    val preferences: StateFlow<LauncherPreferences> = preferencesManager.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LauncherPreferences()
        )

    private val _rawInstalledApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val mathResult: StateFlow<String?> = _searchQuery.map { query ->
        MathEvaluator.evaluate(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen

    private val _selectedAppForMenu = MutableStateFlow<AppInfo?>(null)
    val selectedAppForMenu: StateFlow<AppInfo?> = _selectedAppForMenu

    // Folder Management States
    private val _activeFolderForExpansion = MutableStateFlow<AppFolder?>(null)
    val activeFolderForExpansion: StateFlow<AppFolder?> = _activeFolderForExpansion

    private val _folderBeingEdited = MutableStateFlow<AppFolder?>(null)
    val folderBeingEdited: StateFlow<AppFolder?> = _folderBeingEdited

    private val _isFolderDialogVisible = MutableStateFlow(false)
    val isFolderDialogVisible: StateFlow<Boolean> = _isFolderDialogVisible

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

    // Folders
    val folders: StateFlow<List<AppFolder>> = preferences.map { it.folders }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            preferences.collect { prefs ->
                loadApps()
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setFirstLaunchCompleted(true)
            _currentScreen.value = Screen.HOME
        }
    }

    fun restartOnboarding() {
        viewModelScope.launch {
            preferencesManager.setFirstLaunchCompleted(false)
            _currentScreen.value = Screen.ONBOARDING
        }
    }

    // App Renaming Dialog State
    private val _appBeingRenamed = MutableStateFlow<AppInfo?>(null)
    val appBeingRenamed: StateFlow<AppInfo?> = _appBeingRenamed

    fun openRenameDialog(app: AppInfo) {
        _appBeingRenamed.value = app
        closeAppMenu()
    }

    fun dismissRenameDialog() {
        _appBeingRenamed.value = null
    }

    fun renameApp(app: AppInfo, newName: String) {
        viewModelScope.launch {
            preferencesManager.setCustomAppName(app.id, newName)
            dismissRenameDialog()
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            val currentPrefs = preferences.value
            val list = appRepository.loadInstalledApps(
                pinnedIds = currentPrefs.pinnedAppIds,
                hiddenIds = currentPrefs.hiddenAppIds,
                customLabels = currentPrefs.customAppNames,
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

    // Folder Actions
    fun openFolder(folder: AppFolder) {
        _activeFolderForExpansion.value = folder
    }

    fun closeFolder() {
        _activeFolderForExpansion.value = null
    }

    fun startCreateFolder() {
        _folderBeingEdited.value = null
        _isFolderDialogVisible.value = true
    }

    fun startEditFolder(folder: AppFolder) {
        _folderBeingEdited.value = folder
        _isFolderDialogVisible.value = true
        _activeFolderForExpansion.value = null
    }

    fun dismissFolderDialog() {
        _isFolderDialogVisible.value = false
        _folderBeingEdited.value = null
    }

    fun saveFolder(folder: AppFolder) {
        viewModelScope.launch {
            preferencesManager.saveFolder(folder)
            dismissFolderDialog()
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            preferencesManager.deleteFolder(folderId)
            closeFolder()
        }
    }

    fun getAppsForFolder(folder: AppFolder): List<AppInfo> {
        val all = _rawInstalledApps.value
        return folder.appIds.mapNotNull { id ->
            all.find { it.id == id }
        }
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
        _activeFolderForExpansion.value = null
        _currentScreen.value = Screen.HOME
    }

    // Customization Settings
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

    fun setClockStyle(style: ClockStyle) {
        viewModelScope.launch { preferencesManager.setClockStyle(style) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesManager.setThemeMode(mode) }
    }

    fun setAccentColor(accent: AccentColor) {
        viewModelScope.launch { preferencesManager.setAccentColor(accent) }
    }

    fun setTextSizeScale(scale: TextSizeScale) {
        viewModelScope.launch { preferencesManager.setTextSizeScale(scale) }
    }

    fun setAppAlignment(alignment: AppAlignment) {
        viewModelScope.launch { preferencesManager.setAppAlignment(alignment) }
    }

    fun setCustomGreeting(greeting: String) {
        viewModelScope.launch { preferencesManager.setCustomGreeting(greeting) }
    }

    fun saveQuickNotes(notes: String) {
        viewModelScope.launch { preferencesManager.saveQuickNotes(notes) }
    }

    fun handleBackPressed(): Boolean {
        if (_activeFolderForExpansion.value != null) {
            _activeFolderForExpansion.value = null
            return true
        }
        if (_isFolderDialogVisible.value) {
            dismissFolderDialog()
            return true
        }
        return when (_currentScreen.value) {
            Screen.DRAWER, Screen.SETTINGS, Screen.HIDDEN_APPS, Screen.CONTROL_CENTER, Screen.QUICK_NOTES, Screen.FOLDER_MANAGER, Screen.CALCULATOR, Screen.STORAGE_MANAGER -> {
                _currentScreen.value = Screen.HOME
                _searchQuery.value = ""
                true
            }
            Screen.ONBOARDING, Screen.HOME -> false
        }
    }

    override fun onCleared() {
        super.onCleared()
        PackageChangeReceiver.onPackageChangedListener = null
    }
}
