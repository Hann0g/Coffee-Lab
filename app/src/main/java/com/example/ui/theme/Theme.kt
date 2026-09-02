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
    primary = CoffeePrimaryDark,
    onPrimary = CoffeeOnPrimaryDark,
    primaryContainer = CoffeePrimaryContainerDark,
    onPrimaryContainer = CoffeeOnPrimaryContainerDark,
    secondary = CoffeeSecondaryDark,
    onSecondary = CoffeeOnSecondaryDark,
    secondaryContainer = CoffeeSecondaryContainerDark,
    onSecondaryContainer = CoffeeOnSecondaryContainerDark,
    tertiary = CoffeeTertiaryContainer,
    onTertiary = CoffeeOnTertiaryContainer,
    background = CoffeeBackgroundDark,
    onBackground = CoffeeOnBackgroundDark,
    surface = CoffeeSurfaceDark,
    onSurface = CoffeeOnSurfaceDark,
    surfaceVariant = CoffeeSurfaceVariantDark,
    onSurfaceVariant = CoffeeOnSurfaceVariantDark,
    outline = CoffeeOutlineDark,
    outlineVariant = CoffeeOutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = CoffeePrimary,
    onPrimary = CoffeeOnPrimary,
    primaryContainer = CoffeePrimaryContainer,
    onPrimaryContainer = CoffeeOnPrimaryContainer,
    secondary = CoffeeSecondary,
    onSecondary = CoffeeOnSecondary,
    secondaryContainer = CoffeeSecondaryContainer,
    onSecondaryContainer = CoffeeOnSecondaryContainer,
    tertiary = CoffeeTertiary,
    onTertiary = CoffeeOnTertiary,
    background = CoffeeBackgroundLight,
    onBackground = CoffeeOnBackgroundLight,
    surface = CoffeeSurfaceLight,
    onSurface = CoffeeOnSurfaceLight,
    surfaceVariant = CoffeeSurfaceVariantLight,
    onSurfaceVariant = CoffeeOnSurfaceVariantLight,
    outline = CoffeeOutlineLight,
    outlineVariant = CoffeeOutlineVariantLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep milky coffee brown theme
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
