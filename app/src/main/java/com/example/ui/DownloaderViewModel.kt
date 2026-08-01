package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.data.AppDatabase
import com.example.data.DownloadItem
import com.example.data.DownloadRepository
import com.example.data.ParsedTikTokMedia
import com.example.data.TikTokApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class DownloaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DownloadRepository
    private val apiService = TikTokApiService()
    private val context = getApplication<Application>()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DownloadRepository(database.downloadDao())
    }

    val downloads: StateFlow<List<DownloadItem>> = repository.allDownloads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _isExtracting = MutableStateFlow(false)
    val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

    private val _extractionResult = MutableStateFlow<ParsedTikTokMedia?>(null)
    val extractionResult: StateFlow<ParsedTikTokMedia?> = _extractionResult.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isDownloadWithoutLeaving = MutableStateFlow(true)
    val isDownloadWithoutLeaving: StateFlow<Boolean> = _isDownloadWithoutLeaving.asStateFlow()

    private val _isAutoDownload = MutableStateFlow(false)
    val isAutoDownload: StateFlow<Boolean> = _isAutoDownload.asStateFlow()

    private val _isOfflineSimulated = MutableStateFlow(false)
    val isOfflineSimulated: StateFlow<Boolean> = _isOfflineSimulated.asStateFlow()

    private val _language = MutableStateFlow("ar")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _currentScreen = MutableStateFlow("main")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun onUrlInputChanged(newUrl: String) {
        _urlInput.value = newUrl
        _errorMessage.value = null
        if (_isAutoDownload.value && newUrl.length > 15 && newUrl.contains("tiktok")) {
            extractVideoInfo()
        }
    }

    fun clearInput() {
        _urlInput.value = ""
        _extractionResult.value = null
        _errorMessage.value = null
    }

    fun extractVideoInfo() {
        val url = _urlInput.value.trim()
        if (url.isEmpty()) {
            _errorMessage.value = "الرجاء إدخال رابط تيك توك للتنزيل"
            return
        }

        if (_isOfflineSimulated.value) {
            _errorMessage.value = "فشل الاتصال: يرجى الاتصال بالإنترنت أولاً"
            return
        }

        viewModelScope.launch {
            _isExtracting.value = true
            _errorMessage.value = null
            _extractionResult.value = null

            val result = apiService.extractVideoInfo(url)
            _isExtracting.value = false

            result.fold(
                onSuccess = { parsed ->
                    _extractionResult.value = parsed
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "حدث خطأ غير متوقع أثناء تحليل الرابط"
                }
            )
        }
    }

    fun startDownload(mediaType: String, option: String) {
        val media = _extractionResult.value ?: return

        viewModelScope.launch {
            _isDownloading.value = true
            _downloadProgress.value = 0f

            // ✅ للصوت: استخدم الفيديو بدون علامة مائية وحوله إلى MP3
            val downloadUrl = when (mediaType) {
                "video" -> {
                    if (option == "no_watermark" || option == "بدون علامة مائية") {
                        media.noWatermarkUrl
                    } else {
                        media.watermarkUrl
                    }
                }
                "audio" -> {
                    // استخدم الفيديو كمصدر للصوت
                    media.noWatermarkUrl.ifEmpty { media.watermarkUrl }
                }
                "image" -> media.noWatermarkUrl
                else -> media.noWatermarkUrl
            }

            if (downloadUrl.isEmpty()) {
                _isDownloading.value = false
                _errorMessage.value = "رابط التحميل غير متوفر"
                return@launch
            }

            val success = downloadFile(downloadUrl, media, mediaType)

            if (success) {
                val newItem = DownloadItem(
                    originalUrl = _urlInput.value,
                    title = media.title,
                    authorName = media.authorName,
                    authorHandle = media.authorHandle,
                    thumbnailUrl = media.thumbnailUrl,
                    mediaType = mediaType,
                    noWatermarkUrl = media.noWatermarkUrl,
                    watermarkUrl = media.watermarkUrl,
                    audioUrl = media.audioUrl,
                    fileSize = media.fileSize
                )

                withContext(Dispatchers.IO) {
                    repository.saveDownload(newItem)
                }

                _downloadProgress.value = 1f
                delay(500)
                Toast.makeText(context, "تم التحميل بنجاح ✅", Toast.LENGTH_SHORT).show()
            }

            _isDownloading.value = false
        }
    }

    private suspend fun downloadFile(fileUrl: String, media: ParsedTikTokMedia, mediaType: String): Boolean {
        return withContext(Dispatchers.IO) {
            var downloadedFile: File? = null
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.connectTimeout = 30000
                connection.readTimeout = 30000
                connection.connect()

                val fileSize = connection.contentLength.toLong()
                val inputStream: InputStream = connection.inputStream

                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadDir.exists()) downloadDir.mkdirs()

                val tempExtension = ".mp4"
                val safeFileName = media.title.replace(Regex("[^\\u0600-\\u06FF\\u0750-\\u077Fa-zA-Z0-9\\s]"), "")
                    .take(50).trim() + "_${UUID.randomUUID().toString().take(8)}$tempExtension"

                downloadedFile = File(downloadDir, safeFileName)
                val outputStream = FileOutputStream(downloadedFile)

                val buffer = ByteArray(8192)
                var downloaded = 0L
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloaded += bytesRead

                    if (fileSize > 0) {
                        val progress = (downloaded.toFloat() / fileSize.toFloat())
                        _downloadProgress.value = progress
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                connection.disconnect()

                // ✅ إذا كان النوع "audio"، حول الملف من MP4 إلى MP3
                var finalFile = downloadedFile
                if (mediaType == "audio") {
                    val mp3FileName = safeFileName.replace(".mp4", ".mp3")
                    val mp3File = File(downloadDir, mp3FileName)

                    val result = FFmpeg.execute("-i ${downloadedFile.absolutePath} -vn -acodec libmp3lame -q:a 2 ${mp3File.absolutePath}")
                    if (result == Config.RETURN_CODE_SUCCESS) {
                        downloadedFile.delete() // حذف الملف المؤقت
                        finalFile = mp3File
                    }
                }

                val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = Uri.fromFile(finalFile)
                context.sendBroadcast(mediaScanIntent)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                downloadedFile?.let { if (it.exists()) it.delete() }
                false
            }
        }
    }

    fun deleteDownload(item: DownloadItem) {
        viewModelScope.launch {
            repository.deleteDownload(item)
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setDownloadWithoutLeaving(enabled: Boolean) {
        _isDownloadWithoutLeaving.value = enabled
    }

    fun setAutoDownload(enabled: Boolean) {
        _isAutoDownload.value = enabled
    }

    fun setOfflineSimulated(enabled: Boolean) {
        _isOfflineSimulated.value = enabled
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun setPremium(enabled: Boolean) {
        _isPremium.value = enabled
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
        _currentScreen.value = "main"
    }

    fun navigateToScreen(screen: String) {
        _currentScreen.value = screen
    }
}
