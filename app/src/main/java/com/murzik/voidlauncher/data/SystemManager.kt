package com.murzik.voidlauncher.data

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BatteryStatus(
    val level: Int = 100,
    val isCharging: Boolean = false
)

data class DeviceHardwareInfo(
    val model: String,
    val androidVersion: String,
    val totalRamGb: String,
    val availableRamGb: String,
    val osName: String = "Murzik OS (Void Edition v1.0)"
)

class SystemManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn

    private val _batteryStatus = MutableStateFlow(BatteryStatus())
    val batteryStatus: StateFlow<BatteryStatus> = _batteryStatus

    private var cameraIdWithFlash: String? = null

    init {
        initFlashlight()
        initBatteryReceiver()
    }

    private fun initFlashlight() {
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                if (hasFlash) {
                    cameraIdWithFlash = id
                    break
                }
            }
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    if (cameraId == cameraIdWithFlash) {
                        _isFlashlightOn.value = enabled
                    }
                }
            }, null)
        } catch (_: Exception) {
            // Flashlight unavailable
        }
    }

    fun toggleFlashlight() {
        val id = cameraIdWithFlash ?: return
        try {
            val newState = !_isFlashlightOn.value
            cameraManager.setTorchMode(id, newState)
            _isFlashlightOn.value = newState
        } catch (_: Exception) {
            // Error toggling torch
        }
    }

    private fun initBatteryReceiver() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val percent = if (level != -1 && scale != -1) (level * 100 / scale) else 100
                    val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL
                    _batteryStatus.value = BatteryStatus(level = percent, isCharging = isCharging)
                }
            }
        }, filter)
    }

    // Volume helpers
    fun getMaxMediaVolume(): Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    fun getMediaVolume(): Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    fun setMediaVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    // Hardware Specs
    fun getHardwareInfo(): DeviceHardwareInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalGb = String.format("%.1f GB", memoryInfo.totalMem.toDouble() / (1024 * 1024 * 1024))
        val availGb = String.format("%.1f GB", memoryInfo.availMem.toDouble() / (1024 * 1024 * 1024))

        val deviceName = if (Build.MODEL.contains("17", ignoreCase = true) || Build.MODEL.contains("Xiaomi", ignoreCase = true)) {
            Build.MODEL
        } else {
            "Xiaomi 17T (${Build.MODEL})"
        }

        return DeviceHardwareInfo(
            model = deviceName,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            totalRamGb = totalGb,
            availableRamGb = availGb
        )
    }

    // Direct Settings Shortcuts
    fun openWifiSettings() {
        startIntent(Settings.ACTION_WIFI_SETTINGS)
    }

    fun openBluetoothSettings() {
        startIntent(Settings.ACTION_BLUETOOTH_SETTINGS)
    }

    fun openAirplaneSettings() {
        startIntent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
    }

    fun openSoundSettings() {
        startIntent(Settings.ACTION_SOUND_SETTINGS)
    }

    fun openDisplaySettings() {
        startIntent(Settings.ACTION_DISPLAY_SETTINGS)
    }

    fun openBatterySettings() {
        startIntent(Intent.ACTION_POWER_USAGE_SUMMARY)
    }

    private fun startIntent(action: String) {
        try {
            val intent = Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(fallback)
        }
    }
}
