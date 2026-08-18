package com.example.zheheshima.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2D6F9F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8ECF8),
    onPrimaryContainer = Color(0xFF0B2D43),
    secondary = Color(0xFF477A91),
    secondaryContainer = Color(0xFFDCEEF5),
    background = Color(0xFFF7FAFC),
    onBackground = Color(0xFF17232C),
    surface = Color.White,
    onSurface = Color(0xFF17232C),
    surfaceVariant = Color(0xFFEAF1F5),
    onSurfaceVariant = Color(0xFF52636E),
    outline = Color(0xFF9BAEB8),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC)
)

@Composable
fun ZheHeShiMaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
