package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.PremiumPinkEnd
import com.example.ui.theme.PremiumPurpleStart
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PremiumUpgradeDialog(
    onDismiss: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(4.dp).testTag("premium_upgrade_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.clip(CircleShape).background(Color(0xFFFFF1F2)).padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(stringResource(R.string.special_offer_50), color = CrimsonPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("close_premium_dialog")) {
                        Icon(Icons.Default.Close, stringResource(R.string.close), tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PremiumPurpleStart, PremiumPinkEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Diamond, "Diamond VIP", tint = Color(0xFFFFD700), modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(stringResource(R.string.upgrade_vip), fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary, textAlign = TextAlign.Center)
                Text(stringResource(R.string.enjoy_unlimited), fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PremiumFeatureItem(stringResource(R.string.feature_4k))
                    PremiumFeatureItem(stringResource(R.string.feature_speed))
                    PremiumFeatureItem(stringResource(R.string.feature_no_ads))
                    PremiumFeatureItem(stringResource(R.string.feature_mp3))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlanOptionCard(Modifier.weight(1f), stringResource(R.string.monthly), "$2.99", "/ ${stringResource(R.string.month)}", isSelected = selectedPlan == 0, tag = "plan_monthly", onClick = { selectedPlan = 0 })
                    PlanOptionCard(Modifier.weight(1f), stringResource(R.string.lifetime), "$9.99", stringResource(R.string.once), stringResource(R.string.most_popular_badge), isSelected = selectedPlan == 1, tag = "plan_lifetime", onClick = { selectedPlan = 1 })
                    PlanOptionCard(Modifier.weight(1f), stringResource(R.string.yearly), "$14.99", "/ ${stringResource(R.string.year)}", isSelected = selectedPlan == 2, tag = "plan_yearly", onClick = { selectedPlan = 2 })
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onPurchaseSuccess(); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("subscribe_now_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FlashOn, stringResource(R.string.buy), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.subscribe_now), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumFeatureItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.CheckCircle, "Feature", tint = CrimsonPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PlanOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    price: String,
    period: String,
    badge: String? = null,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0xFFFFF1F2) else Color(0xFFF8F9FA))
            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) CrimsonPrimary else Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (badge != null) {
                Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(price, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = CrimsonPrimary)
            Text(period, fontSize = 9.sp, color = TextSecondary)
        }
    }
}
