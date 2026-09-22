package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ReelVibeDarkColorScheme = darkColorScheme(
    primary = ReelPink,
    onPrimary = ReelWhite,
    primaryContainer = ReelPurple,
    onPrimaryContainer = ReelWhite,
    secondary = ReelCyan,
    onSecondary = ReelDarkObsidian,
    secondaryContainer = ReelDeepViolet,
    onSecondaryContainer = ReelCyan,
    tertiary = ReelGold,
    onTertiary = ReelDarkObsidian,
    background = ReelDarkObsidian,
    onBackground = ReelLightText,
    surface = ReelSurface,
    onSurface = ReelLightText,
    surfaceVariant = ReelSurfaceVariant,
    onSurfaceVariant = ReelSubtleText,
    outline = ReelBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ReelVibeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
