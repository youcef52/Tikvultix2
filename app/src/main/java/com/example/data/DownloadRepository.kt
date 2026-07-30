package com.example.data

import kotlinx.coroutines.flow.Flow

class DownloadRepository(private val dao: DownloadDao) {
    val allDownloads: Flow<List<DownloadItem>> = dao.getAllDownloads()
    val downloadCount: Flow<Int> = dao.getDownloadCount()

    fun getDownloadsByType(type: String): Flow<List<DownloadItem>> {
        return if (type == "all" || type.isEmpty()) {
            dao.getAllDownloads()
        } else {
            dao.getDownloadsByType(type)
        }
    }

    suspend fun saveDownload(item: DownloadItem): Long {
        return dao.insertDownload(item)
    }

    suspend fun deleteDownload(item: DownloadItem) {
        dao.deleteDownload(item)
    }

    suspend fun clearHistory() {
        dao.clearAllDownloads()
    }
}
