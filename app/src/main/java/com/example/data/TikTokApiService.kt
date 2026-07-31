package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class ParsedTikTokMedia(
    val title: String,
    val authorName: String,
    val authorHandle: String,
    val thumbnailUrl: String,
    val mediaType: String,
    val noWatermarkUrl: String,
    val watermarkUrl: String,
    val audioUrl: String,
    val fileSize: String,
    val imageCovers: List<String> = emptyList(),
    val sizeBytes: Long = 0 // الحجم بالبايت للاختيار بين الجودات
)

class TikTokApiService {

    private val API_URL = "https://www.tikwm.com/api/"

    suspend fun extractVideoInfo(rawUrl: String): Result<ParsedTikTokMedia> {
        return withContext(Dispatchers.IO) {
            try {
                val cleanUrl = rawUrl.trim()
                
                if (cleanUrl.isEmpty()) {
                    return@withContext Result.failure(IllegalArgumentException("الرجاء إدخال رابط تيك توك صحيح"))
                }

                val isValidTikTokUrl = cleanUrl.contains("tiktok.com", ignoreCase = true) ||
                        cleanUrl.contains("vt.tiktok", ignoreCase = true) ||
                        cleanUrl.contains("vm.tiktok", ignoreCase = true)

                if (!isValidTikTokUrl) {
                    return@withContext Result.failure(IllegalArgumentException("رابط غير صالح، يرجى التأكد من نسخ رابط تيك توك بشكل صحيح"))
                }

                // استدعاء API الحقيقي
                val apiUrl = "$API_URL?url=$cleanUrl"
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    return@withContext Result.failure(Exception("خطأ في الاتصال بالخادم (كود: $responseCode)"))
                }

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                connection.disconnect()

                val json = JSONObject(response.toString())
                val code = json.optInt("code", -1)

                if (code != 0) {
                    val msg = json.optString("msg", "فشل استخراج بيانات الفيديو")
                    return@withContext Result.failure(Exception(msg))
                }

                val data = json.getJSONObject("data")
                val title = data.optString("title", "بدون عنوان")
                val authorName = data.optJSONObject("author")?.optString("nickname", "مستخدم تيك توك") ?: "مستخدم تيك توك"
                val authorHandle = data.optJSONObject("author")?.optString("unique_id", "@tiktok_user") ?: "@tiktok_user"
                val thumbnailUrl = data.optString("cover", "")
                val noWatermarkUrl = data.optString("play", "")
                val watermarkUrl = data.optString("wmplay", "")
                val audioUrl = data.optString("music", "")
                val images = data.optJSONArray("images")

                val mediaType: String
                val imageCovers: List<String>

                if (images != null && images.length() > 0) {
                    // منشور صور
                    mediaType = "image"
                    val covers = mutableListOf<String>()
                    for (i in 0 until images.length()) {
                        covers.add(images.getString(i))
                    }
                    imageCovers = covers
                } else if (noWatermarkUrl.isNotEmpty()) {
                    mediaType = "video"
                    imageCovers = emptyList()
                } else {
                    mediaType = "audio"
                    imageCovers = emptyList()
                }

                val sizeBytes = data.optLong("size", 0)
                val fileSize = formatFileSize(sizeBytes)

                val parsedMedia = ParsedTikTokMedia(
                    title = title,
                    authorName = authorName,
                    authorHandle = authorHandle,
                    thumbnailUrl = thumbnailUrl,
                    mediaType = mediaType,
                    noWatermarkUrl = noWatermarkUrl,
                    watermarkUrl = watermarkUrl,
                    audioUrl = audioUrl,
                    fileSize = fileSize,
                    imageCovers = imageCovers,
                    sizeBytes = sizeBytes
                )

                Result.success(parsedMedia)
            } catch (e: Exception) {
                Result.failure(Exception("فشل الاتصال: ${e.message}"))
            }
        }
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "غير معروف"
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb < 1) {
            "${bytes / 1024} KB"
        } else {
            String.format("%.1f MB", mb)
        }
    }
}
