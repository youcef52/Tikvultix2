package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApiConfigManager {

    private const val PREFS_NAME = "api_config_prefs"
    private const val KEY_ACTIVE_API = "active_api_url"
    private const val CONFIG_URL = "https://tikvultix-api.simon-dz1992.workers.dev/"

    suspend fun getActiveApiUrl(context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                val config = downloadConfig()
                if (config != null && !config.maintenance) {
                    val workingUrl = findWorkingApi(config)
                    if (workingUrl != null) {
                        saveActiveApi(context, workingUrl)
                        return@withContext workingUrl
                    }
                }
            } catch (_: Exception) { }

            val saved = getSavedApi(context)
            if (saved != null) return@withContext saved

            "https://www.tikwm.com/api/"
        }
    }

    private suspend fun downloadConfig(): ApiConfig? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(CONFIG_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if (connection.responseCode != 200) return@withContext null

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                connection.disconnect()

                val json = JSONObject(response)
                ApiConfig(
                    primary = json.optString("primary", "https://www.tikwm.com/api/"),
                    backup = json.optString("backup", ""),
                    backup2 = json.optString("backup2", ""),
                    latest_version = json.optString("latest_version", "1.0.0"),
                    maintenance = json.optBoolean("maintenance", false)
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    private suspend fun findWorkingApi(config: ApiConfig): String? {
        val urls = listOfNotNull(config.primary, config.backup, config.backup2).filter { it.isNotBlank() }
        for (url in urls) {
            if (testApi(url)) return url
        }
        return null
    }

    private suspend fun testApi(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL("$url?url=test").openConnection() as HttpURLConnection
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                connection.requestMethod = "GET"
                connection.responseCode in 200..499
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun saveActiveApi(context: Context, url: String) {
        getPrefs(context).edit().putString(KEY_ACTIVE_API, url).apply()
    }

    private fun getSavedApi(context: Context): String? {
        return getPrefs(context).getString(KEY_ACTIVE_API, null)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
