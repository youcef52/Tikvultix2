package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DownloadItem
import com.example.data.DownloadRepository
import com.example.data.ParsedTikTokMedia
import com.example.data.TikTokApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DownloadRepository
    private val apiService = TikTokApiService()

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

    // UI State
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

    // Settings & Toggles State
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

    // Navigation State
    private val _currentTab = MutableStateFlow(0) // 0: Home, 1: History
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _currentScreen = MutableStateFlow("main") // "main", "settings", "privacy"
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
            _errorMessage.value = "الرجاء أدخل رابط تيك توك للتنزيل"
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

            // Simulate progress ticks
            for (i in 1..10) {
                delay(150)
                _downloadProgress.value = i / 10f
            }

            _isDownloading.value = false

            // Save completed download item to Room database
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

            repository.saveDownload(newItem)
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
