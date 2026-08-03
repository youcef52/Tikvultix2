package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ApiConfigManager
import com.example.ui.DownloaderViewModel
import com.example.ui.components.AdMobBannerPlaceholder
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.TopAppBarHeader
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MainScreen(
    viewModel: DownloaderViewModel,
    onLanguageChanged: (String) -> Unit
) {
    val unknownErrorText = stringResource(R.string.unknown_error)
    var appState by remember { mutableStateOf<AppState>(AppState.Loading) }

    LaunchedEffect(Unit) {
        val config = ApiConfigManager.getConfig()
        appState = when {
            config.isMaintenance -> AppState.Maintenance
            config.apiUrl != null -> {
                viewModel.updateApiUrl(config.apiUrl)
                AppState.Ready
            }
            else -> {
                val saved = ApiConfigManager.getSavedApiUrl(viewModel.getContext())
                if (saved != null) {
                    viewModel.updateApiUrl(saved)
                    AppState.Ready
                } else {
                    AppState.Error(config.error ?: unknownErrorText)
                }
            }
        }
    }

    when (val state = appState) {
        is AppState.Loading -> LoadingScreen()
        is AppState.Maintenance -> MaintenanceScreen(onRetry = { appState = AppState.Loading })
        is AppState.Error -> ErrorScreen(message = state.message, onRetry = { appState = AppState.Loading })
        is AppState.Ready -> MainAppContent(viewModel = viewModel, onLanguageChanged = onLanguageChanged)
    }
}

sealed class AppState {
    object Loading : AppState()
    object Maintenance : AppState()
    data class Error(val message: String) : AppState()
    object Ready : AppState()
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = CrimsonPrimary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.connecting), fontSize = 16.sp, color = TextSecondary)
        }
    }
}

@Composable
fun MaintenanceScreen(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🛠️", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.maintenance_title), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.maintenance_message), fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.padding(4.dp))
                Text(stringResource(R.string.retry), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("⚠️", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.connection_error), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.padding(4.dp))
                Text(stringResource(R.string.retry), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: DownloaderViewModel, onLanguageChanged: (String) -> Unit) {
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
        if (launchIntent != null) context.startActivity(launchIntent)
        else { val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com")); context.startActivity(browserIntent) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                isDownloadWithoutLeaving = isDownloadWithoutLeaving, onToggleDownloadWithoutLeaving = { viewModel.setDownloadWithoutLeaving(it) },
                isAutoDownload = isAutoDownload, onToggleAutoDownload = { viewModel.setAutoDownload(it) },
                onOpenPremium = { showPremiumDialog = true }, onOpenCustomerSupport = { showSupportDialog = true },
                onJoinCommunity = { showCommunityDialog = true }, onHowToDownload = { showHowToDownloadDialog = true },
                onRecommendFriends = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app_text)) }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.recommend_friends)))
                },
                onManageSubscriptions = { showPremiumDialog = true }, onOpenSettings = { viewModel.navigateToScreen("settings") },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = { if (currentScreen == "main") TopAppBarHeader(isOffline = isOfflineSimulated, onOpenDrawer = { scope.launch { drawerState.open() } }, onOpenTikTok = { openTikTokApp() }, onOpenPremium = { showPremiumDialog = true }) },
            bottomBar = {
                if (currentScreen == "main") {
                    Column {
                        AdMobBannerPlaceholder(adTitle = context.getString(R.string.advertisement))
                        NavigationBar(containerColor = Color.White, tonalElevation = 8.dp, modifier = Modifier.shadow(8.dp).testTag("bottom_navigation_bar")) {
                            NavigationBarItem(selected = currentTab == 0, onClick = { viewModel.selectTab(0) }, icon = { Icon(if (currentTab == 0) Icons.Filled.Home else Icons.Outlined.Home, context.getString(R.string.home)) }, label = { Text(context.getString(R.string.home), fontWeight = FontWeight.Bold) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = CrimsonPrimary, selectedTextColor = CrimsonPrimary, indicatorColor = Color(0xFFFFF1F2), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray), modifier = Modifier.testTag("nav_item_home"))
                            NavigationBarItem(selected = currentTab == 1, onClick = { viewModel.selectTab(1) }, icon = { Icon(if (currentTab == 1) Icons.Filled.History else Icons.Outlined.History, context.getString(R.string.history)) }, label = { Text(context.getString(R.string.history), fontWeight = FontWeight.Bold) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = CrimsonPrimary, selectedTextColor = CrimsonPrimary, indicatorColor = Color(0xFFFFF1F2), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray), modifier = Modifier.testTag("nav_item_history"))
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F9FA))) {
                AnimatedContent(targetState = currentScreen, transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) }, label = "ScreenTransition") { targetScreen ->
                    when (targetScreen) {
                        "settings" -> SettingsScreen(language, { selectedLang ->
                            viewModel.setLanguage(selectedLang)
                            onLanguageChanged(selectedLang)
                        }, isOfflineSimulated, { viewModel.setOfflineSimulated(it) }, { viewModel.navigateToScreen("privacy") }, { viewModel.navigateToScreen("main") })
                        "privacy" -> PrivacyScreen { viewModel.navigateToScreen("settings") }
                        else -> {
                            AnimatedContent(targetState = currentTab, transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) }, label = "TabTransition") { targetTab ->
                                when (targetTab) {
                                    0 -> HomeScreen(urlInput, { viewModel.onUrlInputChanged(it) }, { viewModel.extractVideoInfo() }, { openTikTokApp() }, isExtracting, extractionResult, downloadProgress, isDownloading, errorMessage, { type, opt -> viewModel.startDownload(type, opt); Toast.makeText(context, context.getString(R.string.download_started), Toast.LENGTH_SHORT).show() }, { viewModel.clearInput() })
                                    1 -> HistoryScreen(downloads, { viewModel.deleteDownload(it) }, { viewModel.clearAllDownloads() })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPremiumDialog) PremiumUpgradeDialog(onDismiss = { showPremiumDialog = false }, onPurchaseSuccess = { viewModel.setPremium(true); Toast.makeText(context, context.getString(R.string.premium_success), Toast.LENGTH_LONG).show() })
    if (showSupportDialog) AlertDialog(onDismissRequest = { showSupportDialog = false }, title = { Text(context.getString(R.string.support_title), fontWeight = FontWeight.Bold) }, text = { Text(context.getString(R.string.support_message)) }, confirmButton = { TextButton(onClick = { showSupportDialog = false }) { Text(context.getString(R.string.ok), color = CrimsonPrimary, fontWeight = FontWeight.Bold) } })
    if (showCommunityDialog) AlertDialog(onDismissRequest = { showCommunityDialog = false }, title = { Text(context.getString(R.string.community_title), fontWeight = FontWeight.Bold) }, text = { Text(context.getString(R.string.community_message)) }, confirmButton = { TextButton(onClick = { showCommunityDialog = false; val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/tikvultix_community")); runCatching { context.startActivity(i) } }) { Text(context.getString(R.string.join_now), color = CrimsonPrimary, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = { showCommunityDialog = false }) { Text(context.getString(R.string.cancel)) } })
    if (showHowToDownloadDialog) AlertDialog(onDismissRequest = { showHowToDownloadDialog = false }, title = { Text(context.getString(R.string.how_to_download_title), fontWeight = FontWeight.Bold) }, text = { Text(context.getString(R.string.how_to_download_message)) }, confirmButton = { TextButton(onClick = { showHowToDownloadDialog = false }) { Text(context.getString(R.string.got_it), color = CrimsonPrimary, fontWeight = FontWeight.Bold) } })
}
