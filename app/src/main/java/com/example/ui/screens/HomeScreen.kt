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
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .testTag("home_screen_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 1. INPUT SECTION
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, shape = RoundedCornerShape(16.dp))
                    .testTag("url_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = onUrlInputChange,
                            placeholder = {
                                Text(
                                    text = "الصق رابط TikTok هنا",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            },
                            trailingIcon = {
                                if (urlInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = onClearInput,
                                        modifier = Modifier.size(32.dp)
                                            .testTag("clear_url_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = clipboard.primaryClip
                                            if (clip != null && clip.itemCount > 0) {
                                                val text = clip.getItemAt(0).text?.toString() ?: ""
                                                if (text.isNotEmpty()) {
                                                    onUrlInputChange(text)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                            .testTag("paste_clipboard_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentPaste,
                                            contentDescription = "Paste",
                                            tint = CrimsonPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CrimsonPrimary,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color(0xFFFAFAFA),
                                unfocusedContainerColor = Color(0xFFFAFAFA)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("url_text_field")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = onExtractClick,
                            enabled = !isExtracting,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("main_download_button")
                        ) {
                            if (isExtracting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Download",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "تحميل",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 2. DOWNLOAD RESULT CARD
        if (extractionResult != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                        .testTag("download_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1A1A2E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Media",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = extractionResult.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${extractionResult.authorName} (${extractionResult.authorHandle})",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "الحجم: ${extractionResult.fileSize}",
                                    fontSize = 10.sp,
                                    color = CrimsonPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isDownloading && downloadProgress != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "جاري التحميل...",
                                        fontSize = 10.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${(downloadProgress * 100).toInt()}%",
                                        fontSize = 10.sp,
                                        color = CrimsonPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { downloadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = CrimsonPrimary,
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            DownloadOptionButton(
                                text = "بدون علامة مائية (HD)",
                                icon = Icons.Default.Hd,
                                badge = "الأكثر شعبية",
                                containerColor = CrimsonPrimary,
                                contentColor = Color.White,
                                tag = "btn_download_no_watermark",
                                onClick = { onStartDownload("video", "no_watermark") }
                            )

                            DownloadOptionButton(
                                text = "مع علامة مائية",
                                icon = Icons.Default.Videocam,
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = TextPrimary,
                                tag = "btn_download_with_watermark",
                                onClick = { onStartDownload("video", "with_watermark") }
                            )

                            DownloadOptionButton(
                                text = "الصوت MP3",
                                icon = Icons.Default.LibraryMusic,
                                containerColor = Color(0xFFE0F7FA),
                                contentColor = Color(0xFF00838F),
                                tag = "btn_download_audio_mp3",
                                onClick = { onStartDownload("audio", "audio_mp3") }
                            )

                            if (extractionResult.imageCovers.isNotEmpty()) {
                                DownloadOptionButton(
                                    text = "صور الألبوم",
                                    icon = Icons.Default.Image,
                                    containerColor = Color(0xFFF3E5F5),
                                    contentColor = Color(0xFF7B1FA2),
                                    tag = "btn_download_images_album",
                                    onClick = { onStartDownload("image", "images_album") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. QUICK GUIDE
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_action_guide_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تنزيل سريع بدون علامة مائية ⚡",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "بدون تسجيل دخول · بدون رسوم · بدون سجل",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GuideStepCircle(Icons.Default.Share, "مشاركة", "1")
                        GuideStepCircle(Icons.Default.ContentCopy, "نسخ الرابط", "2")
                        GuideStepCircle(Icons.Default.Launch, "افتح التطبيق", "3")
                    }
                }
            }
        }

        // 4. OPEN TIKTOK BUTTON
        item {
            Button(
                onClick = onOpenTikTokApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(14.dp))
                    .testTag("open_tiktok_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🎵", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "افتح TikTok وانسخ الرابط",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 5. رابط "عرض المزيد من الشروحات"
        item {
            TextButton(
                onClick = { showHowToDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "عرض المزيد من الشروحات",
                    color = CrimsonPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    if (showHowToDialog) {
        HowToDownloadDialog(
            onDismiss = { showHowToDialog = false },
            onOpenTikTok = {
                showHowToDialog = false
                onOpenTikTokApp()
            }
        )
    }
}

@Composable
private fun HowToDownloadDialog(onDismiss: () -> Unit, onOpenTikTok: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("كيفية التحميل", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, "إغلاق", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HowToStep(Icons.Default.Share, "1", "افتح تيك توك واختر الفيديو", "اذهب إلى TikTok واضغط على زر المشاركة (Share) على الفيديو المراد تحميله.")
                HowToStep(Icons.Default.ContentCopy, "2", "انسخ رابط الفيديو", "من القائمة المنبثقة، اختر نسخ الرابط (Copy Link).")
                HowToStep(Icons.Default.Launch, "3", "ارجع للتطبيق والصق الرابط", "افتح TikVultix وسيتم لصق الرابط تلقائياً. اضغط تحميل للتنزيل بدون علامة مائية.")
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenTikTok,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("🎵  ", fontSize = 16.sp)
                Text("افتح TikTok وانسخ الرابط", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("إلغاء", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    )
}

@Composable
private fun HowToStep(icon: ImageVector, stepNumber: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF8F9FA)).padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(CrimsonPrimary), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("$stepNumber. $title", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun GuideStepCircle(icon: ImageVector, label: String, stepNum: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFF1F2)).border(1.5.dp, CrimsonPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = CrimsonPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
    }
}

@Composable
private fun DownloadOptionButton(
    text: String, icon: ImageVector, badge: String? = null,
    containerColor: Color, contentColor: Color, tag: String, onClick: () -> Unit
) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(42.dp).testTag(tag),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, text, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (badge != null) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = 0.3f)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                    Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = contentColor)
                }
            }
        }
    }
}
