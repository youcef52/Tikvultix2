package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DownloadItem
import com.example.ui.components.AdMobBannerPlaceholder
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    downloads: List<DownloadItem>,
    onDeleteDownload: (DownloadItem) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("all") }

    val filteredList = when (selectedFilter) {
        "video" -> downloads.filter { it.mediaType == "video" }
        "image" -> downloads.filter { it.mediaType == "image" }
        "audio" -> downloads.filter { it.mediaType == "audio" }
        else -> downloads
    }

    val downloadHistoryText = stringResource(R.string.download_history)
    val clearAllText = stringResource(R.string.clear_all)
    val filterAllText = stringResource(R.string.filter_all)
    val filterVideoText = stringResource(R.string.filter_video)
    val filterImageText = stringResource(R.string.filter_image)
    val filterAudioText = stringResource(R.string.filter_audio)
    val noDownloadsText = stringResource(R.string.no_downloads)
    val copyLinksText = stringResource(R.string.copy_links_to_download)
    val watchVideoText = stringResource(R.string.watch_video)
    val shareVideoText = stringResource(R.string.share_video)
    val historyAdText = stringResource(R.string.history_ad)
    val emptyText = stringResource(R.string.empty)
    val playText = stringResource(R.string.play)
    val shareText = stringResource(R.string.share)
    val deleteText = stringResource(R.string.delete)

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("history_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$downloadHistoryText (${filteredList.size}) 📁", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            if (downloads.isNotEmpty()) {
                TextButton(onClick = onClearAll, modifier = Modifier.testTag("clear_history_button")) {
                    Icon(Icons.Default.DeleteSweep, clearAllText, tint = CrimsonPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(clearAllText, color = CrimsonPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipItem(filterAllText, selectedFilter == "all", "filter_chip_all") { selectedFilter = "all" }
            FilterChipItem("$filterVideoText 📹", selectedFilter == "video", "filter_chip_video") { selectedFilter = "video" }
            FilterChipItem("$filterImageText 🖼️", selectedFilter == "image", "filter_chip_image") { selectedFilter = "image" }
            FilterChipItem("$filterAudioText 🎵", selectedFilter == "audio", "filter_chip_audio") { selectedFilter = "audio" }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().testTag("empty_history_container"), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(90.dp).clip(CircleShape).background(Color(0xFFFFF1F2)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FolderOpen, emptyText, tint = CrimsonPrimary, modifier = Modifier.size(44.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(noDownloadsText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(copyLinksText, fontSize = 12.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredList, key = { it.id }) { item ->
                    DownloadHistoryItemCard(
                        item = item,
                        playText = playText,
                        shareText = shareText,
                        deleteText = deleteText,
                        watchVideoText = watchVideoText,
                        shareVideoText = shareVideoText,
                        onDelete = { onDeleteDownload(item) },
                        onShare = { url ->
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "$watchVideoText: $url")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, shareVideoText))
                        },
                        onPlay = { url ->
                            val playIntent = Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(url), "video/*") }
                            runCatching { context.startActivity(playIntent) }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }

        AdMobBannerPlaceholder(adTitle = historyAdText, modifier = Modifier.padding(bottom = 12.dp))
    }
}

@Composable
private fun FilterChipItem(label: String, isSelected: Boolean, tag: String, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected, onClick = onClick,
        label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CrimsonPrimary, selectedLabelColor = Color.White, containerColor = Color.White, labelColor = TextPrimary),
        shape = RoundedCornerShape(12.dp), modifier = Modifier.testTag(tag)
    )
}

@Composable
private fun DownloadHistoryItemCard(
    item: DownloadItem,
    playText: String,
    shareText: String,
    deleteText: String,
    watchVideoText: String,
    shareVideoText: String,
    onDelete: () -> Unit,
    onShare: (String) -> Unit,
    onPlay: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.downloadTimestamp))

    Card(
        modifier = Modifier.fillMaxWidth().testTag("download_history_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1E1E24)).clickable { onPlay(item.noWatermarkUrl) }, contentAlignment = Alignment.Center) {
                val mediaIcon = when (item.mediaType) {
                    "image" -> Icons.Default.Image
                    "audio" -> Icons.Default.MusicNote
                    else -> Icons.Default.PlayArrow
                }
                Icon(mediaIcon, playText, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text(item.authorName, fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.fileSize, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formattedDate, fontSize = 9.sp, color = Color.Gray)
                }
            }
            Row {
                IconButton(onClick = { onShare(item.originalUrl) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, shareText, tint = CrimsonPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, deleteText, tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
