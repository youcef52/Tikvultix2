package com.example.data

import kotlinx.coroutines.delay

/**
 * Service handling TikTok video metadata extraction and link parsing.
 * 
 * ============================================================================
 * 🛠 INSTRUCTIONS FOR INJECTING CUSTOM TIKTOK DOWNLOADER API ENDPOINTS:
 * ============================================================================
 * To replace simulated video parsing with your real backend/third-party API:
 * 1. Add Retrofit / Ktor client dependency if connecting to remote REST services.
 * 2. Set your API Base URL (e.g. "https://api.tikwm.com/api/" or RapidAPI endpoint).
 * 3. In `extractVideoInfo(url)`, execute your API GET/POST request passing the video link.
 * 4. Parse the returned JSON response fields (e.g., `data.play`, `data.wmplay`, `data.music`).
 * 5. Return the mapped `ParsedTikTokMedia` object below.
 * ============================================================================
 */
data class ParsedTikTokMedia(
    val title: String,
    val authorName: String,
    val authorHandle: String,
    val thumbnailUrl: String,
    val mediaType: String, // "video", "image", "audio"
    val noWatermarkUrl: String,
    val watermarkUrl: String,
    val audioUrl: String,
    val fileSize: String,
    val imageCovers: List<String> = emptyList()
)

class TikTokApiService {

    suspend fun extractVideoInfo(rawUrl: String): Result<ParsedTikTokMedia> {
        // Simulate network API delay (1.2 seconds)
        delay(1200)

        val cleanUrl = rawUrl.trim()
        if (cleanUrl.isEmpty()) {
            return Result.failure(IllegalArgumentException("الرجاء أدخل رابط تيك توك صحيح"))
        }

        val isValidTikTokUrl = cleanUrl.contains("tiktok.com", ignoreCase = true) || 
                               cleanUrl.contains("vt.tiktok", ignoreCase = true) ||
                               cleanUrl.startsWith("http", ignoreCase = true)

        if (!isValidTikTokUrl) {
            return Result.failure(IllegalArgumentException("رابط غير صالح، يرجى التأكد من نسخ رابط تيك توك بشكل صحيح"))
        }

        // Generate parsed media metadata based on URL keywords or sample fallback
        val isImagePost = cleanUrl.contains("photo") || cleanUrl.contains("image")
        val isAudioOnly = cleanUrl.contains("music") || cleanUrl.contains("sound")

        val title = when {
            isImagePost -> "أحدث التحديات والتقليعات المميزة على تيك توك 📸✨"
            isAudioOnly -> "صوت أصلي شائع - موسيقى تيك توك ترند 2026 🎵"
            cleanUrl.contains("dance") -> "تحدي الرقص الفيروسي 💃🔥 #TikTokDance #Viral"
            cleanUrl.contains("recipe") -> "وصفة طبخ سريعة ولذيذة في أقل من دقيقة 🍳🍕"
            else -> "فيديو تيك توك مميز بدون علامة مائية 🚀 #تيك_توك #ترند"
        }

        val authorName = if (cleanUrl.contains("ar")) "أحمد العتيبي" else "Creative Creator"
        val authorHandle = "@tiktok_creator_official"
        val mediaType = when {
            isImagePost -> "image"
            isAudioOnly -> "audio"
            else -> "video"
        }

        val parsedMedia = ParsedTikTokMedia(
            title = title,
            authorName = authorName,
            authorHandle = authorHandle,
            thumbnailUrl = "https://picsum.photos/seed/${cleanUrl.hashCode()}/400/600",
            mediaType = mediaType,
            noWatermarkUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            watermarkUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            fileSize = if (isImagePost) "4.2 MB" else "18.5 MB",
            imageCovers = listOf(
                "https://picsum.photos/seed/img1/600/600",
                "https://picsum.photos/seed/img2/600/600",
                "https://picsum.photos/seed/img3/600/600"
            )
        )

        return Result.success(parsedMedia)
    }
}
