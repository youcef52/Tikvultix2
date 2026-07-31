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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("history_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(R.string.download_history)} (${filteredList.size}) 📁",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            if (downloads.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = stringResource(R.string.clear_all),
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.clear_all),
                        color = CrimsonPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem(label = stringResource(R.string.filter_all), isSelected = selectedFilter == "all", tag = "filter_chip_all", onClick = { selectedFilter = "all" })
            FilterChipItem(label = "${stringResource(R.string.filter_video)} 📹", isSelected = selectedFilter == "video", tag = "filter_chip_video", onClick = { selectedFilter = "video" })
            FilterChipItem(label = "${stringResource(R.string.filter_image)} 🖼️", isSelected = selectedFilter == "image", tag = "filter_chip_image", onClick = { selectedFilter = "image" })
            FilterChipItem(label = "${stringResource(R.string.filter_audio)} 🎵", isSelected = selectedFilter == "audio", tag = "filter_chip_audio", onClick = { selectedFilter = "audio" })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("empty_history_container"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF1F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = stringResource(R.string.empty),
                            tint = CrimsonPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.no_downloads),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.copy_links_to_download),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    DownloadHistoryItemCard(
                        item = item,
                        onDelete = { onDeleteDownload(item) },
                        onShare = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${stringResource(R.string.watch_video)}: ${item.originalUrl}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, stringResource(R.string.share_video)))
                        },
                        onPlay = {
                            val playIntent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.parse(item.noWatermarkUrl), "video/*")
                            }
                            runCatching { context.startActivity(playIntent) }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }

        AdMobBannerPlaceholder(
            adTitle = stringResource(R.string.history_ad),
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CrimsonPrimary,
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = TextPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag(tag)
    )
}

@Composable
private fun DownloadHistoryItemCard(
    item: DownloadItem,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onPlay: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.downloadTimestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("download_history_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E24))
                    .clickable { onPlay() },
                contentAlignment = Alignment.Center
            ) {
                val mediaIcon = when (item.mediaType) {
                    "image" -> Icons.Default.Image
                    "audio" -> Icons.Default.MusicNote
                    else -> Icons.Default.PlayArrow
                }
                Icon(
                    imageVector = mediaIcon,
                    contentDescription = stringResource(R.string.play),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = item.authorName, fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.fileSize, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formattedDate, fontSize = 9.sp, color = Color.Gray)
                }
            }

            Row {
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, stringResource(R.string.share), tint = CrimsonPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, stringResource(R.string.delete), tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
