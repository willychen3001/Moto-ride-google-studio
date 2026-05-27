package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Supportive teal for system accents
val RadiantTeal = Color(0xFF006A6A)

// Light Color Scheme - Pure M3 Vibrant Palette
private val LightColorScheme = lightColorScheme(
    primary = VibrantPurple,
    onPrimary = Color.White,
    primaryContainer = VibrantLilac,
    onPrimaryContainer = VibrantDarkPurple,
    secondary = VibrantPeach,
    onSecondary = VibrantBronze,
    tertiary = RadiantTeal,
    onTertiary = Color.White,
    background = VibrantLightBg,
    onBackground = VibrantText,
    surface = VibrantCardBg,
    onSurface = VibrantText,
    surfaceVariant = VibrantMapBg,
    onSurfaceVariant = VibrantGrayText,
    outline = VibrantBorder,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

// Dark companion for Vibrant Palette (supports system dark mode dynamically transitions beautifully)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFFFB4AB),
    onSecondary = Color(0xFF690005),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF211F26),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Set default to light theme (Vibrant Palette style)
    content: @Composable () -> Unit
) {
    // We allow user choice, but default is Light Theme for the premium "Vibrant Palette" experience
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
