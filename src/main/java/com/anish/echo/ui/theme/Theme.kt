package com.anish.echo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Mode 1 - Neutral Grey (fallback for dynamic)
private val DarkColorScheme1 = darkColorScheme(
    primary = Color(0xFFB0B0B0),         // Light grey
    onPrimary = Color(0xFF121212),
    secondary = Color(0xFF8A8A8A),       // Medium grey
    onSecondary = Color(0xFF121212),
    tertiary = Color(0xFF6F6F6F),        // Dark grey
    background = Color(0xFF121212),
    onBackground = Color(0xFFE5E5E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF6F6F6F)
)

// Dark Mode 2 - Warm Gold
private val DarkColorScheme2 = darkColorScheme(
    primary = EchoPrimary,
    onPrimary = EchoDarkBackground,
    secondary = EchoPrimaryVariant,
    onSecondary = EchoDarkBackground,
    tertiary = EchoPrimaryVariant,
    background = EchoDarkBackground,
    onBackground = EchoTextPrimary,
    surface = EchoSurface,
    onSurface = EchoTextPrimary,
    surfaceVariant = EchoSurfaceVariant,
    onSurfaceVariant = EchoTextSecondary,
    outline = EchoTextMuted
)

// Theme 3 - Cherry Mocha
private val CherryMochaScheme = darkColorScheme(
    primary = Color(0xFFF6B7AC),         // Coral pink
    onPrimary = Color(0xFF140A08),
    secondary = Color(0xFFE79B90),       // Soft coral
    onSecondary = Color(0xFF140A08),
    tertiary = Color(0xFFFFC1B6),        // Light peach
    background = Color(0xFF140A08),      // Deep mocha
    onBackground = Color(0xFFF5DED9),    // Cream text
    surface = Color(0xFF1C0F0D),         // Dark mocha
    onSurface = Color(0xFFF5DED9),
    surfaceVariant = Color(0xFF2A1613),  // Medium mocha
    onSurfaceVariant = Color(0xFFC6AAA5),
    outline = Color(0xFF9B7E79)
)

// Theme 4 - Light Cream (no harsh whites)
private val LightCreamScheme = lightColorScheme(
    primary = Color(0xFF3A3A3A),         // Dark grey
    onPrimary = Color(0xFFF5F0E8),       // Cream
    secondary = Color(0xFF5A5A5A),       // Medium grey
    onSecondary = Color(0xFFF5F0E8),
    tertiary = Color(0xFFD4C5B0),        // Warm cream
    background = Color(0xFFF5F0E8),      // Cream background
    onBackground = Color(0xFF2A2A2A),    // Dark text
    surface = Color(0xFFFAF7F2),         // Slightly lighter cream
    onSurface = Color(0xFF2A2A2A),
    surfaceVariant = Color(0xFFEDE8E0),  // Muted cream
    onSurfaceVariant = Color(0xFF5A5A5A),
    outline = Color(0xFFD4C5B0)
)

// Theme 5 - Warm Amber (yellow/black/cream - cohesive)
private val YellowScheme = lightColorScheme(
    primary = Color(0xFF3A3A3A),         // Dark grey/black
    onPrimary = Color(0xFFFFF3D0),       // Light cream
    secondary = Color(0xFFD4A000),       // Deep amber
    onSecondary = Color(0xFF2A2A2A),
    tertiary = Color(0xFFE6C28E),        // Warm tan
    background = Color(0xFFFFF3D0),      // Soft cream-yellow
    onBackground = Color(0xFF2A2A2A),    // Dark text
    surface = Color(0xFFFFF8E1),         // Light amber
    onSurface = Color(0xFF2A2A2A),
    surfaceVariant = Color(0xFFFFECB3),  // Warm yellow tint
    onSurfaceVariant = Color(0xFF4A4A4A),
    outline = Color(0xFFD4A000)          // Amber outline
)


enum class ThemeMode {
    DARK_MODE_1,    // Dynamic wallpaper
    DARK_MODE_2,    // Warm Gold
    CHERRY_MOCHA,   // Cherry Mocha dark
    LIGHT,          // Light cream
    YELLOW          // Yellow/black/cream
}

@Composable
fun EchoTheme(
    themeMode: ThemeMode = ThemeMode.DARK_MODE_2,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    val colorScheme = when (themeMode) {
        ThemeMode.DARK_MODE_1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dynamicDarkColorScheme(context)
            } else {
                DarkColorScheme1
            }
        }
        ThemeMode.DARK_MODE_2 -> DarkColorScheme2
        ThemeMode.CHERRY_MOCHA -> CherryMochaScheme
        ThemeMode.LIGHT -> LightCreamScheme
        ThemeMode.YELLOW -> YellowScheme
    }
    
    // Determine if this is a light theme for status bar icons
    val isLightTheme = themeMode == ThemeMode.LIGHT || themeMode == ThemeMode.YELLOW
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

