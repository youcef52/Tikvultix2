package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("privacy_screen"),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top AppBar (RTL Title & Back arrow on the right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("privacy_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1E24)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الخصوصية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E24)
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // SECTION 1: Legal and Policies
                Text(
                    text = "Legal and Policies",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                PrivacyRowSimple(
                    title = "شروط الاستخدام",
                    icon = Icons.Default.Gavel,
                    onClick = {
                        Toast.makeText(context, "شروط الاستخدام", Toast.LENGTH_SHORT).show()
                    },
                    tag = "privacy_tile_terms"
                )

                HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

                PrivacyRowSimple(
                    title = "سياسة الخصوصية",
                    icon = Icons.Default.Policy,
                    onClick = {
                        Toast.makeText(context, "سياسة الخصوصية", Toast.LENGTH_SHORT).show()
                    },
                    tag = "privacy_tile_privacy"
                )

                HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

                PrivacyRowSimple(
                    title = "Cookies policy",
                    icon = Icons.Default.Cookie,
                    onClick = {
                        Toast.makeText(context, "Cookies policy", Toast.LENGTH_SHORT).show()
                    },
                    tag = "privacy_tile_cookies"
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

                PrivacyRowSimple(
                    title = "Data officer",
                    icon = Icons.Default.PersonSearch,
                    onClick = {
                        Toast.makeText(context, "Data protection officer: dpo@tikvultix.app", Toast.LENGTH_LONG).show()
                    },
                    tag = "privacy_tile_data_officer"
                )

                HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

                PrivacyRowSimple(
                    title = "Data portability",
                    icon = Icons.Default.Storage,
                    onClick = {
                        Toast.makeText(context, "Data portability request queued", Toast.LENGTH_SHORT).show()
                    },
                    tag = "privacy_tile_data_portability"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // SECTION 2: Manage information
                Text(
                    text = "Manage information",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                PrivacyRowSimple(
                    title = "Clear cache",
                    icon = Icons.Default.CleaningServices,
                    onClick = {
                        Toast.makeText(context, "Cache cleared successfully (14.2 MB)", Toast.LENGTH_SHORT).show()
                    },
                    tag = "privacy_tile_clear_cache"
                )

                HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
            }
        }
    }
}

@Composable
private fun PrivacyRowSimple(
    title: String,
    icon: ImageVector,
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
                tint = Color(0xFF444444),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E1E24)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = null,
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(20.dp)
        )
    }
}
