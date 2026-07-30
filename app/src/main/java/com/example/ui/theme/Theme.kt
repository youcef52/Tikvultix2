package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CrimsonPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = CrimsonLight,
    onPrimaryContainer = CrimsonDark,
    secondary = CyanAccent,
    onSecondary = TextPrimary,
    secondaryContainer = CyanLight,
    onSecondaryContainer = CyanDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    error = OfflineBannerText,
    errorContainer = OfflineBannerBg
)

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = SurfaceWhite,
    secondary = CyanAccent,
    onSecondary = TextPrimary,
    secondaryContainer = CyanDark,
    onSecondaryContainer = SurfaceWhite,
    background = Color(0xFF121214),
    onBackground = SurfaceWhite,
    surface = Color(0xFF1E1E24),
    onSurface = SurfaceWhite,
    surfaceVariant = Color(0xFF2A2A32),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF383842),
    error = OfflineBannerText,
    errorContainer = OfflineBannerBg
)

@Composable
fun SnapTokTheme(
    darkTheme: Boolean = false, // Clean Light Theme as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
