package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.DownloaderViewModel
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PremiumUpgradeDialog
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.SnapTokTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: DownloaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val language by viewModel.language.collectAsStateWithLifecycle()
            val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SnapTokTheme {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: DownloaderViewModel) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()

    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val isExtracting by viewModel.isExtracting.collectAsStateWithLifecycle()
    val extractionResult by viewModel.extractionResult.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val isDownloadWithoutLeaving by viewModel.isDownloadWithoutLeaving.collectAsStateWithLifecycle()
    val isAutoDownload by viewModel.isAutoDownload.collectAsStateWithLifecycle()
    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()

    var showPremiumDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showCommunityDialog by remember { mutableStateOf(false) }
    var showHowToDownloadDialog by remember { mutableStateOf(false) }

    fun openTikTokApp() {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.zhiliaoapp.musically")
            ?: context.packageManager.getLaunchIntentForPackage("com.ss.android.ugc.trill")
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com"))
            context.startActivity(browserIntent)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                isDownloadWithoutLeaving = isDownloadWithoutLeaving,
                onToggleDownloadWithoutLeaving = { viewModel.setDownloadWithoutLeaving(it) },
                isAutoDownload = isAutoDownload,
                onToggleAutoDownload = { viewModel.setAutoDownload(it) },
                onOpenPremium = { showPremiumDialog = true },
                onOpenCustomerSupport = { showSupportDialog = true },
                onJoinCommunity = { showCommunityDialog = true },
                onHowToDownload = { showHowToDownloadDialog = true },
                onRecommendFriends = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "حمل تطبيق TikVultix لتنزيل فيديوهات تيك توك بدون علامة مائية بسرعة وبجودة عالية!")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "أوصي به للأصدقاء"))
                },
                onManageSubscriptions = { showPremiumDialog = true },
                onOpenSettings = { viewModel.navigateToScreen("settings") },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (currentScreen == "main") {
                    TopAppBarHeader(
                        isOffline = isOfflineSimulated,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onOpenTikTok = { openTikTokApp() },
                        onOpenPremium = { showPremiumDialog = true }
                    )
                }
            },
            bottomBar = {
                if (currentScreen == "main") {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .shadow(8.dp)
                            .testTag("bottom_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { viewModel.selectTab(0) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("الرئيسية", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonPrimary,
                                selectedTextColor = CrimsonPrimary,
                                indicatorColor = Color(0xFFFFF1F2),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("nav_item_home")
                        )

                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { viewModel.selectTab(1) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 1) Icons.Filled.History else Icons.Outlined.History,
                                    contentDescription = "History"
                                )
                            },
                            label = { Text("السجل", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonPrimary,
                                selectedTextColor = CrimsonPrimary,
                                indicatorColor = Color(0xFFFFF1F2),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("nav_item_history")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8F9FA))
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        "settings" -> SettingsScreen(
                            currentLanguage = language,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            isOfflineSimulated = isOfflineSimulated,
                            onToggleOfflineSimulated = { viewModel.setOfflineSimulated(it) },
                            onOpenPrivacy = { viewModel.navigateToScreen("privacy") },
                            onBack = { viewModel.navigateToScreen("main") }
                        )

                        "privacy" -> PrivacyScreen(
                            onBack = { viewModel.navigateToScreen("settings") }
                        )

                        else -> {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "TabTransition"
                            ) { targetTab ->
                                when (targetTab) {
                                    0 -> HomeScreen(
                                        urlInput = urlInput,
                                        onUrlInputChange = { viewModel.onUrlInputChanged(it) },
                                        onExtractClick = { viewModel.extractVideoInfo() },
                                        onOpenTikTokApp = { openTikTokApp() },
                                        isExtracting = isExtracting,
                                        extractionResult = extractionResult,
                                        downloadProgress = downloadProgress,
                                        isDownloading = isDownloading,
                                        errorMessage = errorMessage,
                                        onStartDownload = { type, opt ->
                                            viewModel.startDownload(type, opt)
                                            Toast.makeText(context, "جاري بدء التحميل والمزامنة مع المعرض...", Toast.LENGTH_SHORT).show()
                                        },
                                        onClearInput = { viewModel.clearInput() }
                                    )

                                    1 -> HistoryScreen(
                                        downloads = downloads,
                                        onDeleteDownload = { viewModel.deleteDownload(it) },
                                        onClearAll = { viewModel.clearAllDownloads() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showPremiumDialog) {
        PremiumUpgradeDialog(
            onDismiss = { showPremiumDialog = false },
            onPurchaseSuccess = {
                viewModel.setPremium(true)
                Toast.makeText(context, "تهانينا! تم تفعيل العضوية المميزة بنجاح VIP 👑", Toast.LENGTH_LONG).show()
            }
        )
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("خدمة العملاء 🎧", fontWeight = FontWeight.Bold) },
            text = {
                Text("فريق خدمة العملاء متواجد على مدار 24 ساعة للمساعدة في حال واجهتك أي مشكلة في التحميل.\n\nالبريد الإلكتروني: support@tikvultix.app\nتليجرام: @TikVultixSupport")
            },
            confirmButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text("حسناً", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showCommunityDialog) {
        AlertDialog(
            onDismissRequest = { showCommunityDialog = false },
            title = { Text("مجموعة التعليقات والترندات 💬", fontWeight = FontWeight.Bold) },
            text = {
                Text("انضم إلى مجتمع مستخدمي TikVultix لمشاركة أحدث فيديوهات تيك توك والأصوات الشائعة والحصول على تحديثات فورية.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showCommunityDialog = false
                    val telegramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/tikvultix_community"))
                    runCatching { context.startActivity(telegramIntent) }
                }) {
                    Text("انضمام الآن", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommunityDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showHowToDownloadDialog) {
        AlertDialog(
            onDismissRequest = { showHowToDownloadDialog = false },
            title = { Text("كيفية التحميل بدون علامة مائية 💡", fontWeight = FontWeight.Bold) },
            text = {
                Text("1. افتح تطبيق TikTok واضغط زر المشاركة (Share).\n2. اختر (نسخ الرابط / Copy Link).\n3. افتح تطبيق TikVultix وسيتم بدء التحميل التلقائي لحفظ الفيديو أو الصوت أو الصور.")
            },
            confirmButton = {
                TextButton(onClick = { showHowToDownloadDialog = false }) {
                    Text("فهمت ذلك", color = CrimsonPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
