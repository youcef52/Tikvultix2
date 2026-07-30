package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_items")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val originalUrl: String,
    val title: String,
    val authorName: String,
    val authorHandle: String,
    val thumbnailUrl: String,
    val mediaType: String, // "video", "image", "audio"
    val noWatermarkUrl: String,
    val watermarkUrl: String,
    val audioUrl: String,
    val fileSize: String,
    val downloadTimestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true,
    val isFavorite: Boolean = false
)
