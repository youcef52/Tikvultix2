package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize().testTag("privacy_screen"),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("privacy_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = Color(0xFF1E1E24))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.privacy), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
            }

            HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.legal_policies),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888888),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            PrivacyRowSimple(stringResource(R.string.terms_of_use), Icons.Default.Gavel, { Toast.makeText(context, stringResource(R.string.terms_of_use), Toast.LENGTH_SHORT).show() }, "privacy_tile_terms")
            HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

            PrivacyRowSimple(stringResource(R.string.privacy_policy), Icons.Default.Policy, { Toast.makeText(context, stringResource(R.string.privacy_policy), Toast.LENGTH_SHORT).show() }, "privacy_tile_privacy")
            HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

            PrivacyRowSimple(stringResource(R.string.cookies_policy), Icons.Default.Cookie, { Toast.makeText(context, stringResource(R.string.cookies_policy), Toast.LENGTH_SHORT).show() }, "privacy_tile_cookies")
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

            PrivacyRowSimple(stringResource(R.string.data_officer), Icons.Default.PersonSearch, { Toast.makeText(context, "DPO: dpo@tikvultix.app", Toast.LENGTH_LONG).show() }, "privacy_tile_data_officer")
            HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))

            PrivacyRowSimple(stringResource(R.string.data_portability), Icons.Default.Storage, { Toast.makeText(context, stringResource(R.string.data_portability), Toast.LENGTH_SHORT).show() }, "privacy_tile_data_portability")

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                stringResource(R.string.manage_information),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF888888),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            PrivacyRowSimple(stringResource(R.string.clear_cache), Icons.Default.CleaningServices, { Toast.makeText(context, stringResource(R.string.cache_cleared), Toast.LENGTH_SHORT).show() }, "privacy_tile_clear_cache")
            HorizontalDivider(color = Color(0xFFF1F3F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
        }
    }
}

@Composable
private fun PrivacyRowSimple(title: String, icon: ImageVector, onClick: () -> Unit, tag: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 16.dp).testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, title, tint = Color(0xFF444444), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E1E24))
        }
        Icon(Icons.Default.ChevronLeft, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
    }
}
