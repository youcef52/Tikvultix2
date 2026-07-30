package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

/**
 * AdMob Banner Integration Space
 * 
 * ============================================================================
 * 🛠 INSTRUCTIONS FOR INJECTING ADMOB APP ID AND AD UNIT ID:
 * ============================================================================
 * 1. Add `play-services-ads` dependency in `build.gradle.kts`:
 *    `implementation("com.google.android.gms:play-services-ads:23.0.0")`
 * 2. Add your AdMob App ID to `AndroidManifest.xml`:
 *    <meta-data
 *        android:name="com.google.android.gms.ads.APPLICATION_ID"
 *        android:value="ca-app-pub-3940256099942544~3347511713"/>
 * 3. Replace this placeholder Composable with AndroidView loading `AdView`:
 *    AndroidView(
 *        factory = { context ->
 *            AdView(context).apply {
 *                setAdSize(AdSize.BANNER)
 *                adUnitId = "ca-app-pub-3940256099942544/6300978111" // Your Banner Ad Unit ID
 *                loadAd(AdRequest.Builder().build())
 *            }
 *        }
 *    )
 * ============================================================================
 */
@Composable
fun AdMobBannerPlaceholder(
    modifier: Modifier = Modifier,
    adTitle: String = "إعلان رعاية / Ad Space"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("admob_banner_placeholder"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Ad",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "إعلان AdMob",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = adTitle,
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}
