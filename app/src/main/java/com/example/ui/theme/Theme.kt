package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BoldPurplePrimaryDark,
    onPrimary = BoldPurpleOnPrimaryDark,
    primaryContainer = BoldPurplePrimaryContainerDark,
    onPrimaryContainer = BoldPurpleOnPrimaryContainerDark,
    secondary = BoldSecondaryDark,
    onSecondary = BoldOnSecondaryDark,
    secondaryContainer = BoldSecondaryContainerDark,
    onSecondaryContainer = BoldOnSecondaryContainerDark,
    tertiary = BoldTertiaryContainer,
    onTertiary = BoldOnTertiaryContainer,
    background = BoldBackgroundDark,
    onBackground = BoldOnBackgroundDark,
    surface = BoldSurfaceDark,
    onSurface = BoldOnSurfaceDark,
    surfaceVariant = BoldSurfaceVariantDark,
    onSurfaceVariant = BoldOnSurfaceVariantDark,
    outline = BoldOutlineDark,
    outlineVariant = BoldOutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = BoldPurplePrimary,
    onPrimary = BoldPurpleOnPrimary,
    primaryContainer = BoldPurplePrimaryContainer,
    onPrimaryContainer = BoldPurpleOnPrimaryContainer,
    secondary = BoldSecondary,
    onSecondary = BoldOnSecondary,
    secondaryContainer = BoldSecondaryContainer,
    onSecondaryContainer = BoldOnSecondaryContainer,
    tertiary = BoldTertiary,
    onTertiary = BoldOnTertiary,
    background = BoldBackgroundLight,
    onBackground = BoldOnBackgroundLight,
    surface = BoldSurfaceLight,
    onSurface = BoldOnSurfaceLight,
    surfaceVariant = BoldSurfaceVariantLight,
    onSurfaceVariant = BoldOnSurfaceVariantLight,
    outline = BoldOutlineLight,
    outlineVariant = BoldOutlineVariantLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep handcrafted Bold Typography theme
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

