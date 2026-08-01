package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ParsedTikTokMedia
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    urlInput: String,
    onUrlInputChange: (String) -> Unit,
    onExtractClick: () -> Unit,
    onOpenTikTokApp: () -> Unit,
    isExtracting: Boolean,
    extractionResult: ParsedTikTokMedia?,
    downloadProgress: Float?,
    isDownloading: Boolean,
    errorMessage: String?,
    onStartDownload: (String, String) -> Unit,
    onClearInput: () -> Unit
) {
    val context = LocalContext.current
    var showHowToDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("home_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. INPUT SECTION - مكبر
        item {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)).testTag("url_input_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = onUrlInputChange,
                            placeholder = { Text(stringResource(R.string.paste_link_here), fontSize = 14.sp, color = Color.Gray) },
                            trailingIcon = {
                                if (urlInput.isNotEmpty()) {
                                    IconButton(onClick = onClearInput, modifier = Modifier.size(40.dp).testTag("clear_url_button")) {
                                        Icon(Icons.Default.Clear, stringResource(R.string.clear), tint = Color.Gray, modifier = Modifier.size(20.dp))
                                    }
                                } else {
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = clipboard.primaryClip
                                        if (clip != null && clip.itemCount > 0) {
                                            val text = clip.getItemAt(0).text?.toString() ?: ""
                                            if (text.isNotEmpty()) onUrlInputChange(text)
                                        }
                                    }, modifier = Modifier.size(40.dp).testTag("paste_clipboard_button")) {
                                        Icon(Icons.Default.ContentPaste, stringResource(R.string.paste), tint = CrimsonPrimary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CrimsonPrimary, unfocusedBorderColor = Color(0xFFE5E7EB), focusedContainerColor = Color(0xFFFAFAFA), unfocusedContainerColor = Color(0xFFFAFAFA)),
                            modifier = Modifier.weight(1f).height(58.dp).testTag("url_text_field")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onExtractClick, enabled = !isExtracting,
                            shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            modifier = Modifier.height(58.dp).testTag("main_download_button")
                        ) {
                            if (isExtracting) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                            else Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, stringResource(R.string.download), tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(R.string.download), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    if (errorMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // 2. DOWNLOAD RESULT CARD
        if (extractionResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)).testTag("download_result_card"),
                    shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // ✅ Video thumbnail box: shows the actual pasted
                            // video's cover image (thumbnailUrl) with a play
                            // icon overlay, instead of a plain black box.
                            Box(
                                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF1A1A2E)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (extractionResult.thumbnailUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = extractionResult.thumbnailUrl,
                                        contentDescription = stringResource(R.string.media),
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Icon(
                                    Icons.Default.PlayArrow,
                                    stringResource(R.string.media),
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                        .padding(4.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(extractionResult.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Text("${extractionResult.authorName} (${extractionResult.authorHandle})", fontSize = 12.sp, color = TextSecondary)
                                Text("${stringResource(R.string.size)}: ${extractionResult.fileSize}", fontSize = 12.sp, color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (isDownloading && downloadProgress != null) {
                            Spacer(Modifier.height(12.dp))
                            Column {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(stringResource(R.string.downloading), fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text("${(downloadProgress * 100).toInt()}%", fontSize = 12.sp, color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(progress = { downloadProgress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = CrimsonPrimary, trackColor = Color(0xFFF1F5F9))
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DownloadOptionButton(stringResource(R.string.no_watermark_hd), Icons.Default.Hd, stringResource(R.string.most_popular), CrimsonPrimary, Color.White, "btn_download_no_watermark") { onStartDownload("video", "no_watermark") }
                            DownloadOptionButton(stringResource(R.string.download_stories), Icons.Default.Image, null, Color(0xFFF1F5F9), TextPrimary, "btn_download_stories") { onStartDownload("video", "with_watermark") }
                            DownloadOptionButton(stringResource(R.string.audio_mp3), Icons.Default.LibraryMusic, null, Color(0xFFE0F7FA), Color(0xFF00838F), "btn_download_audio_mp3") { onStartDownload("audio", "audio_mp3") }
                            if (extractionResult.imageCovers.isNotEmpty()) {
                                DownloadOptionButton(stringResource(R.string.album_images), Icons.Default.Image, null, Color(0xFFF3E5F5), Color(0xFF7B1FA2), "btn_download_images_album") { onStartDownload("image", "images_album") }
                            }
                        }
                    }
                }
            }
        }

        // 3. QUICK GUIDE - مكبر
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("quick_action_guide_card"),
                shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.quick_download_no_watermark), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(stringResource(R.string.no_login_no_fees), fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        GuideStepCircle(Icons.Default.Share, stringResource(R.string.share_step))
                        GuideStepCircle(Icons.Default.ContentCopy, stringResource(R.string.copy_link_step))
                        GuideStepCircle(Icons.Default.Launch, stringResource(R.string.open_app_step))
                    }
                }
            }
        }

        item {
            Button(
                onClick = onOpenTikTokApp,
                modifier = Modifier.fillMaxWidth().height(54.dp).shadow(4.dp, RoundedCornerShape(16.dp)).testTag("open_tiktok_button"),
                shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("🎵", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.open_tiktok_copy_link), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        item {
            TextButton(onClick = { showHowToDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.show_more_guides), color = CrimsonPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showHowToDialog) {
        HowToDownloadDialog(onDismiss = { showHowToDialog = false }, onOpenTikTok = { showHowToDialog = false; onOpenTikTokApp() })
    }
}

@Composable
private fun HowToDownloadDialog(onDismiss: () -> Unit, onOpenTikTok: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = Color.White, shape = RoundedCornerShape(20.dp),
        title = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.how_to_download_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Close, stringResource(R.string.close), tint = Color.Gray) }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HowToStep(Icons.Default.Share, "1", stringResource(R.string.dialog_step1_title), stringResource(R.string.dialog_step1_desc))
                HowToStep(Icons.Default.ContentCopy, "2", stringResource(R.string.dialog_step2_title), stringResource(R.string.dialog_step2_desc))
                HowToStep(Icons.Default.Launch, "3", stringResource(R.string.dialog_step3_title), stringResource(R.string.dialog_step3_desc))
            }
        },
        confirmButton = {
            Button(onClick = onOpenTikTok, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("🎵  ", fontSize = 16.sp)
                Text(stringResource(R.string.open_tiktok_copy_link), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel), color = Color.Gray, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    )
}

@Composable
private fun HowToStep(icon: ImageVector, stepNumber: String, title: String, description: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF8F9FA)).padding(12.dp), verticalAlignment = Alignment.Top) {
        Box(Modifier.size(36.dp).clip(CircleShape).background(CrimsonPrimary), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(10.dp))
        Column {
            Text("$stepNumber. $title", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(description, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun GuideStepCircle(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFFFF1F2)).border(2.dp, CrimsonPrimary, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = CrimsonPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
    }
}

@Composable
private fun DownloadOptionButton(text: String, icon: ImageVector, badge: String?, containerColor: Color, contentColor: Color, tag: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(48.dp).testTag(tag), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, text, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            if (badge != null) {
                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.3f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = contentColor)
                }
            }
        }
    }
}
