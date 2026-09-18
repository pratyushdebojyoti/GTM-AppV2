package com.gotechmedia.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Enforced Dark Theme Color Scheme.
 * Adheres strictly to the "Dark mode only" requirement with Apple-inspired obsidian aesthetic.
 */
private val GoTechDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = ObsidianCanvas,
    primaryContainer = ObsidianSurfaceElevated,
    onPrimaryContainer = ElectricCyan,
    secondary = SoftIndigo,
    onSecondary = ObsidianCanvas,
    secondaryContainer = ObsidianSurfaceVariant,
    onSecondaryContainer = SoftIndigo,
    tertiary = EmeraldSuccess,
    onTertiary = ObsidianCanvas,
    background = ObsidianCanvas,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle
)

/**
 * Main application theme wrapper.
 * Strictly enforces dark mode across all system interfaces and devices.
 */
@Composable
fun GoTechMediaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = GoTechDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ObsidianCanvas.toArgb()
                window.navigationBarColor = ObsidianCanvas.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                // False means light status bar text on dark background
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
