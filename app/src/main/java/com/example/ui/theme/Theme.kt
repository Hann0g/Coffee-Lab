package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getLightColorScheme(themeColor: AppThemeColor): ColorScheme {
    return when (themeColor) {
        AppThemeColor.PIXEL_JRPG -> darkColorScheme(
            primary = PixelJrpgPrimary,
            onPrimary = PixelJrpgOnPrimary,
            primaryContainer = PixelJrpgPrimaryContainer,
            onPrimaryContainer = PixelJrpgOnPrimaryContainer,
            secondary = PixelJrpgSecondary,
            onSecondary = PixelJrpgOnSecondary,
            secondaryContainer = PixelJrpgSecondaryContainer,
            onSecondaryContainer = PixelJrpgOnSecondaryContainer,
            tertiary = PixelJrpgTertiary,
            onTertiary = PixelJrpgOnTertiary,
            background = PixelJrpgBackgroundLight,
            onBackground = PixelJrpgOnBackgroundLight,
            surface = PixelJrpgSurfaceLight,
            onSurface = PixelJrpgOnSurfaceLight,
            surfaceVariant = PixelJrpgSurfaceVariantLight,
            onSurfaceVariant = PixelJrpgOnSurfaceVariantLight,
            outline = PixelJrpgOutlineLight,
            outlineVariant = PixelJrpgOutlineVariantLight
        )
        AppThemeColor.TAVERN_PARCHMENT -> lightColorScheme(
            primary = TavernPrimary,
            onPrimary = TavernOnPrimary,
            primaryContainer = TavernPrimaryContainer,
            onPrimaryContainer = TavernOnPrimaryContainer,
            secondary = TavernSecondary,
            onSecondary = TavernOnSecondary,
            secondaryContainer = TavernSecondaryContainer,
            onSecondaryContainer = TavernOnSecondaryContainer,
            tertiary = TavernTertiary,
            onTertiary = TavernOnTertiary,
            background = TavernBackgroundLight,
            onBackground = TavernOnBackgroundLight,
            surface = TavernSurfaceLight,
            onSurface = TavernOnSurfaceLight,
            surfaceVariant = TavernSurfaceVariantLight,
            onSurfaceVariant = TavernOnSurfaceVariantLight,
            outline = TavernOutlineLight,
            outlineVariant = TavernOutlineVariantLight
        )
        AppThemeColor.MILKY_COFFEE -> lightColorScheme(
            primary = MilkyCoffeePrimary,
            onPrimary = MilkyCoffeeOnPrimary,
            primaryContainer = MilkyCoffeePrimaryContainer,
            onPrimaryContainer = MilkyCoffeeOnPrimaryContainer,
            secondary = MilkyCoffeeSecondary,
            onSecondary = MilkyCoffeeOnSecondary,
            secondaryContainer = MilkyCoffeeSecondaryContainer,
            onSecondaryContainer = MilkyCoffeeOnSecondaryContainer,
            tertiary = MilkyCoffeeTertiary,
            onTertiary = MilkyCoffeeOnTertiary,
            background = MilkyCoffeeBackgroundLight,
            onBackground = MilkyCoffeeOnBackgroundLight,
            surface = MilkyCoffeeSurfaceLight,
            onSurface = MilkyCoffeeOnSurfaceLight,
            surfaceVariant = MilkyCoffeeSurfaceVariantLight,
            onSurfaceVariant = MilkyCoffeeOnSurfaceVariantLight,
            outline = MilkyCoffeeOutlineLight,
            outlineVariant = MilkyCoffeeOutlineVariantLight
        )
        AppThemeColor.MATCHA_GREEN -> lightColorScheme(
            primary = MatchaPrimary,
            onPrimary = MatchaOnPrimary,
            primaryContainer = MatchaPrimaryContainer,
            onPrimaryContainer = MatchaOnPrimaryContainer,
            secondary = MatchaSecondary,
            onSecondary = MatchaOnSecondary,
            secondaryContainer = MatchaSecondaryContainer,
            onSecondaryContainer = MatchaOnSecondaryContainer,
            tertiary = MatchaTertiary,
            onTertiary = MatchaOnTertiary,
            background = MatchaBackgroundLight,
            onBackground = MatchaOnBackgroundLight,
            surface = MatchaSurfaceLight,
            onSurface = MatchaOnSurfaceLight,
            surfaceVariant = MatchaSurfaceVariantLight,
            onSurfaceVariant = MatchaOnSurfaceVariantLight,
            outline = MatchaOutlineLight,
            outlineVariant = MatchaOutlineVariantLight
        )
        AppThemeColor.ESPRESSO_AMBER -> lightColorScheme(
            primary = EspressoPrimary,
            onPrimary = EspressoOnPrimary,
            primaryContainer = EspressoPrimaryContainer,
            onPrimaryContainer = EspressoOnPrimaryContainer,
            secondary = EspressoSecondary,
            onSecondary = EspressoOnSecondary,
            secondaryContainer = EspressoSecondaryContainer,
            onSecondaryContainer = EspressoOnSecondaryContainer,
            tertiary = EspressoTertiary,
            onTertiary = EspressoOnTertiary,
            background = EspressoBackgroundLight,
            onBackground = EspressoOnBackgroundLight,
            surface = EspressoSurfaceLight,
            onSurface = EspressoOnSurfaceLight,
            surfaceVariant = EspressoSurfaceVariantLight,
            onSurfaceVariant = EspressoOnSurfaceVariantLight,
            outline = EspressoOutlineLight,
            outlineVariant = EspressoOutlineVariantLight
        )
        AppThemeColor.BERRY_ROASTER -> lightColorScheme(
            primary = BerryPrimary,
            onPrimary = BerryOnPrimary,
            primaryContainer = BerryPrimaryContainer,
            onPrimaryContainer = BerryOnPrimaryContainer,
            secondary = BerrySecondary,
            onSecondary = BerryOnSecondary,
            secondaryContainer = BerrySecondaryContainer,
            onSecondaryContainer = BerryOnSecondaryContainer,
            tertiary = BerryTertiary,
            onTertiary = BerryOnTertiary,
            background = BerryBackgroundLight,
            onBackground = BerryOnBackgroundLight,
            surface = BerrySurfaceLight,
            onSurface = BerryOnSurfaceLight,
            surfaceVariant = BerrySurfaceVariantLight,
            onSurfaceVariant = BerryOnSurfaceVariantLight,
            outline = BerryOutlineLight,
            outlineVariant = BerryOutlineVariantLight
        )
        AppThemeColor.NORDIC_SLATE -> lightColorScheme(
            primary = NordicPrimary,
            onPrimary = NordicOnPrimary,
            primaryContainer = NordicPrimaryContainer,
            onPrimaryContainer = NordicOnPrimaryContainer,
            secondary = NordicSecondary,
            onSecondary = NordicOnSecondary,
            secondaryContainer = NordicSecondaryContainer,
            onSecondaryContainer = NordicOnSecondaryContainer,
            tertiary = NordicTertiary,
            onTertiary = NordicOnTertiary,
            background = NordicBackgroundLight,
            onBackground = NordicOnBackgroundLight,
            surface = NordicSurfaceLight,
            onSurface = NordicOnSurfaceLight,
            surfaceVariant = NordicSurfaceVariantLight,
            onSurfaceVariant = NordicOnSurfaceVariantLight,
            outline = NordicOutlineLight,
            outlineVariant = NordicOutlineVariantLight
        )
    }
}

fun getDarkColorScheme(themeColor: AppThemeColor): ColorScheme {
    return when (themeColor) {
        AppThemeColor.PIXEL_JRPG -> darkColorScheme(
            primary = PixelJrpgPrimaryDark,
            onPrimary = PixelJrpgOnPrimaryDark,
            primaryContainer = PixelJrpgPrimaryContainerDark,
            onPrimaryContainer = PixelJrpgOnPrimaryContainerDark,
            secondary = PixelJrpgSecondaryDark,
            onSecondary = PixelJrpgOnSecondaryDark,
            secondaryContainer = PixelJrpgSecondaryContainerDark,
            onSecondaryContainer = PixelJrpgOnSecondaryContainerDark,
            tertiary = PixelJrpgTertiaryDark,
            onTertiary = PixelJrpgOnTertiaryDark,
            background = PixelJrpgBackgroundDark,
            onBackground = PixelJrpgOnBackgroundDark,
            surface = PixelJrpgSurfaceDark,
            onSurface = PixelJrpgOnSurfaceDark,
            surfaceVariant = PixelJrpgSurfaceVariantDark,
            onSurfaceVariant = PixelJrpgOnSurfaceVariantDark,
            outline = PixelJrpgOutlineDark,
            outlineVariant = PixelJrpgOutlineVariantDark
        )
        AppThemeColor.TAVERN_PARCHMENT -> darkColorScheme(
            primary = Color(0xFFD4A373),
            onPrimary = Color(0xFF2C1908),
            primaryContainer = Color(0xFF4E3014),
            onPrimaryContainer = Color(0xFFFEEAD8),
            secondary = Color(0xFFA3B899),
            onSecondary = Color(0xFF1E2E16),
            secondaryContainer = Color(0xFF33442A),
            onSecondaryContainer = Color(0xFFE2EEDC),
            tertiary = Color(0xFFE2847A),
            onTertiary = Color(0xFF44120D),
            background = Color(0xFF1A130E),
            onBackground = Color(0xFFF3E7DC),
            surface = Color(0xFF241A13),
            onSurface = Color(0xFFF3E7DC),
            surfaceVariant = Color(0xFF3B2E24),
            onSurfaceVariant = Color(0xFFD8C7B8),
            outline = Color(0xFFB59A82),
            outlineVariant = Color(0xFF5E4939)
        )
        AppThemeColor.MILKY_COFFEE -> darkColorScheme(
            primary = MilkyCoffeePrimaryDark,
            onPrimary = MilkyCoffeeOnPrimaryDark,
            primaryContainer = MilkyCoffeePrimaryContainerDark,
            onPrimaryContainer = MilkyCoffeeOnPrimaryContainerDark,
            secondary = MilkyCoffeeSecondaryDark,
            onSecondary = MilkyCoffeeOnSecondaryDark,
            secondaryContainer = MilkyCoffeeSecondaryContainerDark,
            onSecondaryContainer = MilkyCoffeeOnSecondaryContainerDark,
            tertiary = MilkyCoffeeTertiary,
            onTertiary = MilkyCoffeeOnTertiary,
            background = MilkyCoffeeBackgroundDark,
            onBackground = MilkyCoffeeOnBackgroundDark,
            surface = MilkyCoffeeSurfaceDark,
            onSurface = MilkyCoffeeOnSurfaceDark,
            surfaceVariant = MilkyCoffeeSurfaceVariantDark,
            onSurfaceVariant = MilkyCoffeeOnSurfaceVariantDark,
            outline = MilkyCoffeeOutlineDark,
            outlineVariant = MilkyCoffeeOutlineVariantDark
        )
        AppThemeColor.MATCHA_GREEN -> darkColorScheme(
            primary = MatchaPrimaryDark,
            onPrimary = MatchaOnPrimaryDark,
            primaryContainer = MatchaPrimaryContainerDark,
            onPrimaryContainer = MatchaOnPrimaryContainerDark,
            secondary = MatchaSecondaryDark,
            onSecondary = MatchaOnSecondaryDark,
            secondaryContainer = MatchaSecondaryContainerDark,
            onSecondaryContainer = MatchaOnSecondaryContainerDark,
            tertiary = MatchaTertiary,
            onTertiary = MatchaOnTertiary,
            background = MatchaBackgroundDark,
            onBackground = MatchaOnBackgroundDark,
            surface = MatchaSurfaceDark,
            onSurface = MatchaOnSurfaceDark,
            surfaceVariant = MatchaSurfaceVariantDark,
            onSurfaceVariant = MatchaOnSurfaceVariantDark,
            outline = MatchaOutlineDark,
            outlineVariant = MatchaOutlineVariantDark
        )
        AppThemeColor.ESPRESSO_AMBER -> darkColorScheme(
            primary = EspressoPrimaryDark,
            onPrimary = EspressoOnPrimaryDark,
            primaryContainer = EspressoPrimaryContainerDark,
            onPrimaryContainer = EspressoOnPrimaryContainerDark,
            secondary = EspressoSecondaryDark,
            onSecondary = EspressoOnSecondaryDark,
            secondaryContainer = EspressoSecondaryContainerDark,
            onSecondaryContainer = EspressoOnSecondaryContainerDark,
            tertiary = EspressoTertiary,
            onTertiary = EspressoOnTertiary,
            background = EspressoBackgroundDark,
            onBackground = EspressoOnBackgroundDark,
            surface = EspressoSurfaceDark,
            onSurface = EspressoOnSurfaceDark,
            surfaceVariant = EspressoSurfaceVariantDark,
            onSurfaceVariant = EspressoOnSurfaceVariantDark,
            outline = EspressoOutlineDark,
            outlineVariant = EspressoOutlineVariantDark
        )
        AppThemeColor.BERRY_ROASTER -> darkColorScheme(
            primary = BerryPrimaryDark,
            onPrimary = BerryOnPrimaryDark,
            primaryContainer = BerryPrimaryContainerDark,
            onPrimaryContainer = BerryOnPrimaryContainerDark,
            secondary = BerrySecondaryDark,
            onSecondary = BerryOnSecondaryDark,
            secondaryContainer = BerrySecondaryContainerDark,
            onSecondaryContainer = BerryOnSecondaryContainerDark,
            tertiary = BerryTertiary,
            onTertiary = BerryOnTertiary,
            background = BerryBackgroundDark,
            onBackground = BerryOnBackgroundDark,
            surface = BerrySurfaceDark,
            onSurface = BerryOnSurfaceDark,
            surfaceVariant = BerrySurfaceVariantDark,
            onSurfaceVariant = BerryOnSurfaceVariantDark,
            outline = BerryOutlineDark,
            outlineVariant = BerryOutlineVariantDark
        )
        AppThemeColor.NORDIC_SLATE -> darkColorScheme(
            primary = NordicPrimaryDark,
            onPrimary = NordicOnPrimaryDark,
            primaryContainer = NordicPrimaryContainerDark,
            onPrimaryContainer = NordicOnPrimaryContainerDark,
            secondary = NordicSecondaryDark,
            onSecondary = NordicOnSecondaryDark,
            secondaryContainer = NordicSecondaryContainerDark,
            onSecondaryContainer = NordicOnSecondaryContainerDark,
            tertiary = NordicTertiary,
            onTertiary = NordicOnTertiary,
            background = NordicBackgroundDark,
            onBackground = NordicOnBackgroundDark,
            surface = NordicSurfaceDark,
            onSurface = NordicOnSurfaceDark,
            surfaceVariant = NordicSurfaceVariantDark,
            onSurfaceVariant = NordicOnSurfaceVariantDark,
            outline = NordicOutlineDark,
            outlineVariant = NordicOutlineVariantDark
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: AppThemeColor = AppThemeColor.PIXEL_JRPG,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> getDarkColorScheme(themeColor)
        else -> getLightColorScheme(themeColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
