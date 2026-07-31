package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ParsedTikTokMedia
import com.example.ui.components.AdMobBannerPlaceholder
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
    var selectedTutorialTab by remember { mutableStateOf(0) }

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
                    Text(
                        text = "أدخل رابط تيك توك للتنزيل ⚡",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = onUrlInputChange,
                            placeholder = {
                                Text(
                                    text = "إلصق الرابط هنا...",
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

        // 3. QUICK GUIDE - مع horizontalScroll
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GuideStepCircle(
                            icon = Icons.Default.Share,
                            label = "مشاركة",
                            stepNum = "1"
                        )
                        Text(text = "→", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                        GuideStepCircle(
                            icon = Icons.Default.ContentCopy,
                            label = "نسخ الرابط",
                            stepNum = "2"
                        )
                        Text(text = "→", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                        GuideStepCircle(
                            icon = Icons.Default.Launch,
                            label = "افتح التطبيق",
                            stepNum = "3"
                        )
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

        // 5. TUTORIAL TABS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .testTag("tutorial_tabs_section")
            ) {
                Text(
                    text = "دليل الشرح خطوة بخطوة 📘",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = selectedTutorialTab,
                    containerColor = Color(0xFFF8F9FA),
                    contentColor = CrimsonPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTutorialTab]),
                            color = CrimsonPrimary
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTutorialTab == 0,
                        onClick = { selectedTutorialTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Videocam, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("فيديو", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTutorialTab == 1,
                        onClick = { selectedTutorialTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("صورة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTutorialTab == 2,
                        onClick = { selectedTutorialTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("موسيقى", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTutorialTab) {
                    0 -> TutorialStepCard(
                        step1Title = "افتح فيديو تيك توك",
                        step1Desc = "اضغط على زر المشاركة (Share) أسفل الفيديو.",
                        step2Title = "انسخ رابط الفيديو",
                        step2Desc = "انقر على أيقونة نسخ الرابط.",
                        step3Title = "الصق الرابط هنا",
                        step3Desc = "افتح TikVultix واضغط تحميل."
                    )
                    1 -> TutorialStepCard(
                        step1Title = "افتح ألبوم صور تيك توك",
                        step1Desc = "اختر منشور الصور الذي تريد حفظه.",
                        step2Title = "انسخ رابط المشاركة",
                        step2Desc = "اضغط مشاركة ثم نسخ الرابط.",
                        step3Title = "تحميل الصور دفعة واحدة",
                        step3Desc = "اضغط زر تحميل للحصول على كل الصور بجودة HD."
                    )
                    2 -> TutorialStepCard(
                        step1Title = "افتح مقطع الصوت",
                        step1Desc = "اضغط على اسطوانة الصوت في الأسفل.",
                        step2Title = "انسخ رابط الصوت",
                        step2Desc = "اضغط مشاركة ثم انسخ الرابط.",
                        step3Title = "تحميل بصيغة MP3",
                        step3Desc = "احفظ الصوت للاستماع بدون إنترنت."
                    )
                }
            }
        }

        // 6. AD
        item {
            AdMobBannerPlaceholder(adTitle = "إعلان")
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun GuideStepCircle(
    icon: ImageVector,
    label: String,
    stepNum: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF1F2))
                .border(1.5.dp, CrimsonPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CrimsonPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun DownloadOptionButton(
    text: String,
    icon: ImageVector,
    badge: String? = null,
    containerColor: Color,
    contentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag(tag),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = text, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = contentColor)
                }
            }
        }
    }
}

@Composable
private fun TutorialStepCard(
    step1Title: String,
    step1Desc: String,
    step2Title: String,
    step2Desc: String,
    step3Title: String,
    step3Desc: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        StepRowItem(number = "1", title = step1Title, desc = step1Desc)
        StepRowItem(number = "2", title = step2Title, desc = step2Desc)
        StepRowItem(number = "3", title = step3Title, desc = step3Desc)
    }
}

@Composable
private fun StepRowItem(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8F9FA))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(CrimsonPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = desc, fontSize = 10.sp, color = TextSecondary)
        }
    }
}
