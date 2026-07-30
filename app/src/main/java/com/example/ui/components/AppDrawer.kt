package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PremiumPinkEnd
import com.example.ui.theme.PremiumPurpleStart
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppDrawerContent(
    isDownloadWithoutLeaving: Boolean,
    onToggleDownloadWithoutLeaving: (Boolean) -> Unit,
    isAutoDownload: Boolean,
    onToggleAutoDownload: (Boolean) -> Unit,
    onOpenPremium: () -> Unit,
    onOpenCustomerSupport: () -> Unit,
    onJoinCommunity: () -> Unit,
    onHowToDownload: () -> Unit,
    onRecommendFriends: () -> Unit,
    onManageSubscriptions: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight()
            .testTag("app_navigation_drawer"),
        drawerContainerColor = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // 1. Premium Upgrade Card (Purple/Blue Gradient Banner)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(PremiumPurpleStart, PremiumPinkEnd)
                            )
                        )
                        .clickable {
                            onCloseDrawer()
                            onOpenPremium()
                        }
                        .padding(16.dp)
                        .testTag("drawer_premium_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Premium",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ترقية إلى العضوية المميزة",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تحميل غير محدود وبدون إعلانات!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Toggle Switches Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    // Switch 1: Download without leaving TikTok
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تنزيل دون مغادرة تيك توك",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "مراقبة التخزين التلقائي للروابط",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = isDownloadWithoutLeaving,
                            onCheckedChange = onToggleDownloadWithoutLeaving,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CrimsonPrimary
                            ),
                            modifier = Modifier.testTag("switch_download_without_leaving")
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Switch 2: Auto Download
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "التحميل التلقائي",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "تنزيل الفيديو فور نسخ الرابط",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = isAutoDownload,
                            onCheckedChange = onToggleAutoDownload,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CrimsonPrimary
                            ),
                            modifier = Modifier.testTag("switch_auto_download")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Drawer Menu Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(vertical = 6.dp)
                ) {
                    DrawerMenuItem(
                        icon = Icons.Default.SupportAgent,
                        title = "خدمة العملاء",
                        onClick = {
                            onCloseDrawer()
                            onOpenCustomerSupport()
                        },
                        tag = "menu_customer_support"
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Group,
                        title = "انضم إلى مجموعة التعليقات لدينا",
                        onClick = {
                            onCloseDrawer()
                            onJoinCommunity()
                        },
                        tag = "menu_join_community"
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.HelpOutline,
                        title = "كيفية التحميل",
                        onClick = {
                            onCloseDrawer()
                            onHowToDownload()
                        },
                        tag = "menu_how_to_download"
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Share,
                        title = "أوصي به للأصدقاء",
                        onClick = {
                            onCloseDrawer()
                            onRecommendFriends()
                        },
                        tag = "menu_recommend_friends"
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Star,
                        title = "إدارة الاشتراك",
                        onClick = {
                            onCloseDrawer()
                            onManageSubscriptions()
                        },
                        tag = "menu_manage_subscriptions"
                    )

                    DrawerMenuItem(
                        icon = Icons.Default.Settings,
                        title = "الإعدادات",
                        onClick = {
                            onCloseDrawer()
                            onOpenSettings()
                        },
                        tag = "menu_settings"
                    )
                }
            }

            // 4. Bottom Ad Banner Placeholder Integration
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                AdMobBannerPlaceholder(adTitle = "مساحة إعلانات القائمة الجانبية")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "TikVultix v2.5.0(871)",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = CrimsonPrimary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
