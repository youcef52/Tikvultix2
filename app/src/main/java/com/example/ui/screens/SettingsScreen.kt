package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LanguageItem(val code: String, val name: String)

val ALL_LANGUAGES = listOf(
    LanguageItem("ar", "العربية"),
    LanguageItem("en", "English (US/UK)"),
    LanguageItem("es", "Español"),
    LanguageItem("fr", "Français"),
    LanguageItem("de", "Deutsch"),
    LanguageItem("it", "Italiano"),
    LanguageItem("pt", "Português"),
    LanguageItem("ru", "Русский"),
    LanguageItem("tr", "Türkçe"),
    LanguageItem("zh_cn", "中文 (简体)"),
    LanguageItem("zh_tw", "中文 (繁體)"),
    LanguageItem("ja", "日本語"),
    LanguageItem("ko", "한국어"),
    LanguageItem("hi", "हिन्दी"),
    LanguageItem("ur", "اردو"),
    LanguageItem("id", "Bahasa Indonesia"),
    LanguageItem("vi", "Tiếng Việt"),
    LanguageItem("th", "ภาษาไทย"),
    LanguageItem("pa", "Punjabi"),
    LanguageItem("bn", "Bengali"),
    LanguageItem("fa", "Persian"),
    LanguageItem("sw", "Swahili")
)

val CrimsonActionColor = Color(0xFFFF2A55)

@Composable
fun SettingsScreen(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    isOfflineSimulated: Boolean,
    onToggleOfflineSimulated: (Boolean) -> Unit,
    onOpenPrivacy: () -> Unit,
    onBack: () -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    val currentLangObj = ALL_LANGUAGES.find { it.code == currentLanguage }
    val currentLangDisplay = currentLangObj?.name ?: "العربية"

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // AppBar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E1E24)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الإعدادات",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E24)
                )
            }

            HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                SettingsRowSimple(
                    icon = Icons.Default.Public,
                    title = "اللغة",
                    subtitle = currentLangDisplay,
                    onClick = { showLanguageDialog = true },
                    tag = "setting_item_language"
                )

                HorizontalDivider(
                    color = Color(0xFFF1F3F5),
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 56.dp)
                )

                SettingsRowSimple(
                    icon = Icons.Default.Lock,
                    title = "الخصوصية",
                    subtitle = null,
                    onClick = onOpenPrivacy,
                    tag = "setting_item_privacy"
                )

                HorizontalDivider(
                    color = Color(0xFFF1F3F5),
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 56.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TikVultix v2.5.0(871)",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguageCode = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { selectedCode ->
                onLanguageChange(selectedCode)
                showLanguageDialog = false
            }
        )
    }
}

@Composable
private fun SettingsRowSimple(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF333333),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E1E24)
                )
                if (!subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF888888),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = null,
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    var selectedCode by remember { mutableStateOf(currentLanguageCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "اللغة",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E24),
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(ALL_LANGUAGES, key = { it.code }) { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCode = lang.code }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lang.name,
                                fontSize = 14.sp,
                                fontWeight = if (selectedCode == lang.code) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedCode == lang.code) CrimsonActionColor else Color(0xFF222222)
                            )

                            RadioButton(
                                selected = selectedCode == lang.code,
                                onClick = { selectedCode = lang.code },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CrimsonActionColor,
                                    unselectedColor = Color(0xFFCCCCCC)
                                )
                            )
                        }
                        HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(
                    onClick = { onLanguageSelected(selectedCode) },
                    modifier = Modifier.testTag("language_dialog_confirm_button")
                ) {
                    Text(
                        text = "تغيير",
                        color = CrimsonActionColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "إلغاء",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    )
}
