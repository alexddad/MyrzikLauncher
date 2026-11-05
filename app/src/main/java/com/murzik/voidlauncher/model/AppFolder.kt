package com.murzik.voidlauncher.model

import androidx.compose.runtime.Immutable
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

@Immutable
data class AppFolder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val emoji: String = "📁",
    val colorHex: String = "#FFFFFF",
    val appIds: List<String> = emptyList(),
    val isPinnedToHome: Boolean = true
) {
    fun toJson(): String {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("emoji", emoji)
        json.put("colorHex", colorHex)
        val arr = JSONArray()
        appIds.forEach { arr.put(it) }
        json.put("appIds", arr)
        json.put("isPinnedToHome", isPinnedToHome)
        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): AppFolder? {
            return try {
                val json = JSONObject(jsonStr)
                val appIdsList = mutableListOf<String>()
                val arr = json.optJSONArray("appIds")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        appIdsList.add(arr.getString(i))
                    }
                }
                AppFolder(
                    id = json.optString("id", UUID.randomUUID().toString()),
                    name = json.optString("name", "Новая папка"),
                    emoji = json.optString("emoji", "📁"),
                    colorHex = json.optString("colorHex", "#FFFFFF"),
                    appIds = appIdsList,
                    isPinnedToHome = json.optBoolean("isPinnedToHome", true)
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}
