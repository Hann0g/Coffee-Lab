package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemeColor(
    val id: String,
    val displayName: String,
    val description: String,
    val previewColor: Color,
    val secondaryPreview: Color,
    val requiredCoffees: Int = 0
) {
    PIXEL_JRPG(
        id = "pixel_jrpg",
        displayName = "16-Bit Guild (JRPG)",
        description = "Royal blue tavern HUD & gold pixel borders",
        previewColor = Color(0xFF101C46),
        secondaryPreview = Color(0xFFF7C844),
        requiredCoffees = 0
    ),
    TAVERN_PARCHMENT(
        id = "tavern_parchment",
        displayName = "Tavern Parchment",
        description = "Warm quest map with sepia ink & brass",
        previewColor = Color(0xFFF2E6CE),
        secondaryPreview = Color(0xFF8C532B),
        requiredCoffees = 0
    ),
    MILKY_COFFEE(
        id = "milky_coffee",
        displayName = "Milky Coffee",
        description = "Warm latte & creamy mocha",
        previewColor = Color(0xFF704828),
        secondaryPreview = Color(0xFFF3E7DC),
        requiredCoffees = 0
    ),
    MATCHA_GREEN(
        id = "matcha_green",
        displayName = "Matcha Green",
        description = "Fresh green tea & botanical notes",
        previewColor = Color(0xFF2E6B34),
        secondaryPreview = Color(0xFFD8ECD8),
        requiredCoffees = 1
    ),
    ESPRESSO_AMBER(
        id = "espresso_amber",
        displayName = "Espresso Amber",
        description = "Deep dark roast & warm caramel",
        previewColor = Color(0xFF8F4314),
        secondaryPreview = Color(0xFFFCE6D6),
        requiredCoffees = 10
    ),
    BERRY_ROASTER(
        id = "berry_roaster",
        displayName = "Berry Roaster",
        description = "Fruity origin notes & wild hibiscus",
        previewColor = Color(0xFF8E2A59),
        secondaryPreview = Color(0xFFFDD9E8),
        requiredCoffees = 25
    ),
    NORDIC_SLATE(
        id = "nordic_slate",
        displayName = "Nordic Slate",
        description = "Cold brew & Scandinavian slate",
        previewColor = Color(0xFF285D7C),
        secondaryPreview = Color(0xFFDAEBF5),
        requiredCoffees = 40
    );

    fun isUnlocked(trackedCoffees: Int): Boolean = trackedCoffees >= requiredCoffees

    companion object {
        fun fromId(id: String?): AppThemeColor {
            return entries.find { it.id == id } ?: PIXEL_JRPG
        }
    }
}

// 0. 16-Bit Pixel Art JRPG Palette (Royal Blue HUD & Treasure Gold)
val PixelJrpgPrimary = Color(0xFFF7C844)            // 16-bit Gold
val PixelJrpgOnPrimary = Color(0xFF0D1432)          // Midnight Ink
val PixelJrpgPrimaryContainer = Color(0xFF1F326E)   // Midnight Navy Inset
val PixelJrpgOnPrimaryContainer = Color(0xFFFFEFA8) // Soft Gold
val PixelJrpgSecondary = Color(0xFF38BDF8)          // Mana Cyan
val PixelJrpgOnSecondary = Color(0xFF082F49)
val PixelJrpgSecondaryContainer = Color(0xFF193B68)
val PixelJrpgOnSecondaryContainer = Color(0xFFBAE6FD)
val PixelJrpgTertiary = Color(0xFFF43F5E)           // Health Potion Crimson
val PixelJrpgOnTertiary = Color(0xFFFFFFFF)
val PixelJrpgBackgroundLight = Color(0xFF0F1836)    // Classic JRPG Window Dark Blue
val PixelJrpgOnBackgroundLight = Color(0xFFF8FAFC)
val PixelJrpgSurfaceLight = Color(0xFF162554)       // Command Box Blue
val PixelJrpgOnSurfaceLight = Color(0xFFF8FAFC)
val PixelJrpgSurfaceVariantLight = Color(0xFF1E3272)
val PixelJrpgOnSurfaceVariantLight = Color(0xFFCBD5E1)
val PixelJrpgOutlineLight = Color(0xFFF7C844)       // Gold Pixel Border
val PixelJrpgOutlineVariantLight = Color(0xFF2E468A)

val PixelJrpgPrimaryDark = Color(0xFFF7C844)
val PixelJrpgOnPrimaryDark = Color(0xFF0A0F26)
val PixelJrpgPrimaryContainerDark = Color(0xFF182858)
val PixelJrpgOnPrimaryContainerDark = Color(0xFFFFEFA8)
val PixelJrpgSecondaryDark = Color(0xFF38BDF8)
val PixelJrpgOnSecondaryDark = Color(0xFF082F49)
val PixelJrpgSecondaryContainerDark = Color(0xFF152F54)
val PixelJrpgOnSecondaryContainerDark = Color(0xFFBAE6FD)
val PixelJrpgTertiaryDark = Color(0xFFFB7185)
val PixelJrpgOnTertiaryDark = Color(0xFF4C0519)
val PixelJrpgBackgroundDark = Color(0xFF090E22)
val PixelJrpgOnBackgroundDark = Color(0xFFF1F5F9)
val PixelJrpgSurfaceDark = Color(0xFF101B40)
val PixelJrpgOnSurfaceDark = Color(0xFFF1F5F9)
val PixelJrpgSurfaceVariantDark = Color(0xFF1A2A60)
val PixelJrpgOnSurfaceVariantDark = Color(0xFF94A3B8)
val PixelJrpgOutlineDark = Color(0xFFF7C844)
val PixelJrpgOutlineVariantDark = Color(0xFF253974)

// Tavern Parchment Palette
val TavernPrimary = Color(0xFF8C4A1F)
val TavernOnPrimary = Color(0xFFFFFFFF)
val TavernPrimaryContainer = Color(0xFFEBD8BE)
val TavernOnPrimaryContainer = Color(0xFF331705)
val TavernSecondary = Color(0xFF4A6B34)
val TavernOnSecondary = Color(0xFFFFFFFF)
val TavernSecondaryContainer = Color(0xFFD8E6CC)
val TavernOnSecondaryContainer = Color(0xFF18290D)
val TavernTertiary = Color(0xFFA63A3A)
val TavernOnTertiary = Color(0xFFFFFFFF)
val TavernBackgroundLight = Color(0xFFF6EEDD)
val TavernOnBackgroundLight = Color(0xFF2B1D12)
val TavernSurfaceLight = Color(0xFFFCF7ED)
val TavernOnSurfaceLight = Color(0xFF2B1D12)
val TavernSurfaceVariantLight = Color(0xFFECE0C8)
val TavernOnSurfaceVariantLight = Color(0xFF544234)
val TavernOutlineLight = Color(0xFFC4A882)
val TavernOutlineVariantLight = Color(0xFFDCC8AA)

// Milky Coffee Theme Palette
val MilkyCoffeePrimary = Color(0xFF704828)
val MilkyCoffeeOnPrimary = Color(0xFFFFFFFF)
val MilkyCoffeePrimaryContainer = Color(0xFFF3E7DC)
val MilkyCoffeeOnPrimaryContainer = Color(0xFF2C1505)
val MilkyCoffeeSecondary = Color(0xFF8C6D53)
val MilkyCoffeeOnSecondary = Color(0xFFFFFFFF)
val MilkyCoffeeSecondaryContainer = Color(0xFFEDE3D9)
val MilkyCoffeeOnSecondaryContainer = Color(0xFF2E1F14)
val MilkyCoffeeTertiary = Color(0xFFA07855)
val MilkyCoffeeOnTertiary = Color(0xFFFFFFFF)
val MilkyCoffeeBackgroundLight = Color(0xFFFAF7F2)
val MilkyCoffeeOnBackgroundLight = Color(0xFF201A16)
val MilkyCoffeeSurfaceLight = Color(0xFFFFFFFF)
val MilkyCoffeeOnSurfaceLight = Color(0xFF201A16)
val MilkyCoffeeSurfaceVariantLight = Color(0xFFF2ECE4)
val MilkyCoffeeOnSurfaceVariantLight = Color(0xFF53463E)
val MilkyCoffeeOutlineLight = Color(0xFFD6C8BC)
val MilkyCoffeeOutlineVariantLight = Color(0xFFE8DDD2)

val MilkyCoffeePrimaryDark = Color(0xFFE2BC9B)
val MilkyCoffeeOnPrimaryDark = Color(0xFF45240B)
val MilkyCoffeePrimaryContainerDark = Color(0xFF5E3719)
val MilkyCoffeeOnPrimaryContainerDark = Color(0xFFF8EADC)
val MilkyCoffeeSecondaryDark = Color(0xFFD6C3B3)
val MilkyCoffeeOnSecondaryDark = Color(0xFF3A2D22)
val MilkyCoffeeSecondaryContainerDark = Color(0xFF524338)
val MilkyCoffeeOnSecondaryContainerDark = Color(0xFFF2E7DD)
val MilkyCoffeeBackgroundDark = Color(0xFF171310)
val MilkyCoffeeOnBackgroundDark = Color(0xFFEDE4DC)
val MilkyCoffeeSurfaceDark = Color(0xFF211B17)
val MilkyCoffeeOnSurfaceDark = Color(0xFFEDE4DC)
val MilkyCoffeeSurfaceVariantDark = Color(0xFF3D332B)
val MilkyCoffeeOnSurfaceVariantDark = Color(0xFFD6C8BC)
val MilkyCoffeeOutlineDark = Color(0xFF9E8E82)
val MilkyCoffeeOutlineVariantDark = Color(0xFF53463E)

// Matcha Green Theme Palette
val MatchaPrimary = Color(0xFF2E6B34)
val MatchaOnPrimary = Color(0xFFFFFFFF)
val MatchaPrimaryContainer = Color(0xFFD8ECD8)
val MatchaOnPrimaryContainer = Color(0xFF09210C)
val MatchaSecondary = Color(0xFF53634F)
val MatchaOnSecondary = Color(0xFFFFFFFF)
val MatchaSecondaryContainer = Color(0xFFD6E8D0)
val MatchaOnSecondaryContainer = Color(0xFF111F0F)
val MatchaTertiary = Color(0xFF386567)
val MatchaOnTertiary = Color(0xFFFFFFFF)
val MatchaBackgroundLight = Color(0xFFF7FAF7)
val MatchaOnBackgroundLight = Color(0xFF191D19)
val MatchaSurfaceLight = Color(0xFFFFFFFF)
val MatchaOnSurfaceLight = Color(0xFF191D19)
val MatchaSurfaceVariantLight = Color(0xFFDEE5D8)
val MatchaOnSurfaceVariantLight = Color(0xFF424940)
val MatchaOutlineLight = Color(0xFFC2CCC0)
val MatchaOutlineVariantLight = Color(0xFFDCE6DA)

val MatchaPrimaryDark = Color(0xFF95D79B)
val MatchaOnPrimaryDark = Color(0xFF003912)
val MatchaPrimaryContainerDark = Color(0xFF155220)
val MatchaOnPrimaryContainerDark = Color(0xFFB1F4B6)
val MatchaSecondaryDark = Color(0xFFBACBB4)
val MatchaOnSecondaryDark = Color(0xFF263423)
val MatchaSecondaryContainerDark = Color(0xFF3C4B38)
val MatchaOnSecondaryContainerDark = Color(0xFFD6E8D0)
val MatchaBackgroundDark = Color(0xFF111511)
val MatchaOnBackgroundDark = Color(0xFFE1E4DF)
val MatchaSurfaceDark = Color(0xFF191D19)
val MatchaOnSurfaceDark = Color(0xFFE1E4DF)
val MatchaSurfaceVariantDark = Color(0xFF2F372E)
val MatchaOnSurfaceVariantDark = Color(0xFFC2CCC0)
val MatchaOutlineDark = Color(0xFF8C9388)
val MatchaOutlineVariantDark = Color(0xFF424940)

// Espresso Amber Theme Palette
val EspressoPrimary = Color(0xFF8F4314)
val EspressoOnPrimary = Color(0xFFFFFFFF)
val EspressoPrimaryContainer = Color(0xFFFCE6D6)
val EspressoOnPrimaryContainer = Color(0xFF351201)
val EspressoSecondary = Color(0xFF765848)
val EspressoOnSecondary = Color(0xFFFFFFFF)
val EspressoSecondaryContainer = Color(0xFFF4DFD4)
val EspressoOnSecondaryContainer = Color(0xFF2B160B)
val EspressoTertiary = Color(0xFF675F31)
val EspressoOnTertiary = Color(0xFFFFFFFF)
val EspressoBackgroundLight = Color(0xFFFCF8F5)
val EspressoOnBackgroundLight = Color(0xFF201B17)
val EspressoSurfaceLight = Color(0xFFFFFFFF)
val EspressoOnSurfaceLight = Color(0xFF201B17)
val EspressoSurfaceVariantLight = Color(0xFFF4ECE4)
val EspressoOnSurfaceVariantLight = Color(0xFF52443C)
val EspressoOutlineLight = Color(0xFFD7C8BD)
val EspressoOutlineVariantLight = Color(0xFFEAE0D6)

val EspressoPrimaryDark = Color(0xFFFFB590)
val EspressoOnPrimaryDark = Color(0xFF542100)
val EspressoPrimaryContainerDark = Color(0xFF733207)
val EspressoOnPrimaryContainerDark = Color(0xFFFFDCC9)
val EspressoSecondaryDark = Color(0xFFE6BEAB)
val EspressoOnSecondaryDark = Color(0xFF432B1D)
val EspressoSecondaryContainerDark = Color(0xFF5C4132)
val EspressoOnSecondaryContainerDark = Color(0xFFFFDCC9)
val EspressoBackgroundDark = Color(0xFF18120E)
val EspressoOnBackgroundDark = Color(0xFFEDE0D9)
val EspressoSurfaceDark = Color(0xFF211A15)
val EspressoOnSurfaceDark = Color(0xFFEDE0D9)
val EspressoSurfaceVariantDark = Color(0xFF3C312A)
val EspressoOnSurfaceVariantDark = Color(0xFFD7C8BD)
val EspressoOutlineDark = Color(0xFF9E8D82)
val EspressoOutlineVariantDark = Color(0xFF52443C)

// Berry Roaster Theme Palette
val BerryPrimary = Color(0xFF8E2A59)
val BerryOnPrimary = Color(0xFFFFFFFF)
val BerryPrimaryContainer = Color(0xFFFDD9E8)
val BerryOnPrimaryContainer = Color(0xFF3B0822)
val BerrySecondary = Color(0xFF745663)
val BerryOnSecondary = Color(0xFFFFFFFF)
val BerrySecondaryContainer = Color(0xFFF3DDE6)
val BerryOnSecondaryContainer = Color(0xFF2B1520)
val BerryTertiary = Color(0xFF7C5635)
val BerryOnTertiary = Color(0xFFFFFFFF)
val BerryBackgroundLight = Color(0xFFFCF7F9)
val BerryOnBackgroundLight = Color(0xFF201A1C)
val BerrySurfaceLight = Color(0xFFFFFFFF)
val BerryOnSurfaceLight = Color(0xFF201A1C)
val BerrySurfaceVariantLight = Color(0xFFF2E7EC)
val BerryOnSurfaceVariantLight = Color(0xFF514349)
val BerryOutlineLight = Color(0xFFD5C4CB)
val BerryOutlineVariantLight = Color(0xFFE8DBE1)

val BerryPrimaryDark = Color(0xFFFFB0D0)
val BerryOnPrimaryDark = Color(0xFF560030)
val BerryPrimaryContainerDark = Color(0xFF721243)
val BerryOnPrimaryContainerDark = Color(0xFFFFD8E6)
val BerrySecondaryDark = Color(0xFFE2BDCC)
val BerryOnSecondaryDark = Color(0xFF422935)
val BerrySecondaryContainerDark = Color(0xFF5B3F4B)
val BerryOnSecondaryContainerDark = Color(0xFFFFD8E7)
val BerryBackgroundDark = Color(0xFF171114)
val BerryOnBackgroundDark = Color(0xFFECE0E4)
val BerrySurfaceDark = Color(0xFF201A1D)
val BerryOnSurfaceDark = Color(0xFFECE0E4)
val BerrySurfaceVariantDark = Color(0xFF3B3036)
val BerryOnSurfaceVariantDark = Color(0xFFD5C4CB)
val BerryOutlineDark = Color(0xFF9E8E95)
val BerryOutlineVariantDark = Color(0xFF514349)

// Nordic Slate Theme Palette
val NordicPrimary = Color(0xFF285D7C)
val NordicOnPrimary = Color(0xFFFFFFFF)
val NordicPrimaryContainer = Color(0xFFDAEBF5)
val NordicOnPrimaryContainer = Color(0xFF031E2D)
val NordicSecondary = Color(0xFF4F616D)
val NordicOnSecondary = Color(0xFFFFFFFF)
val NordicSecondaryContainer = Color(0xFFD2E5F3)
val NordicOnSecondaryContainer = Color(0xFF0B1D28)
val NordicTertiary = Color(0xFF625B71)
val NordicOnTertiary = Color(0xFFFFFFFF)
val NordicBackgroundLight = Color(0xFFF6F9FB)
val NordicOnBackgroundLight = Color(0xFF181C1F)
val NordicSurfaceLight = Color(0xFFFFFFFF)
val NordicOnSurfaceLight = Color(0xFF181C1F)
val NordicSurfaceVariantLight = Color(0xFFDFE6EB)
val NordicOnSurfaceVariantLight = Color(0xFF42474B)
val NordicOutlineLight = Color(0xFFC3CAD0)
val NordicOutlineVariantLight = Color(0xFFDCE2E7)

val NordicPrimaryDark = Color(0xFF98CCF0)
val NordicOnPrimaryDark = Color(0xFF00344D)
val NordicPrimaryContainerDark = Color(0xFF064B6A)
val NordicOnPrimaryContainerDark = Color(0xFFC9E6FA)
val NordicSecondaryDark = Color(0xFFB7C9D7)
val NordicOnSecondaryDark = Color(0xFF21323D)
val NordicSecondaryContainerDark = Color(0xFF384954)
val NordicOnSecondaryContainerDark = Color(0xFFD2E5F3)
val NordicBackgroundDark = Color(0xFF101417)
val NordicOnBackgroundDark = Color(0xFFDFE3E6)
val NordicSurfaceDark = Color(0xFF181C1F)
val NordicOnSurfaceDark = Color(0xFFDFE3E6)
val NordicSurfaceVariantDark = Color(0xFF2D353A)
val NordicOnSurfaceVariantDark = Color(0xFFC3CAD0)
val NordicOutlineDark = Color(0xFF8D949A)
val NordicOutlineVariantDark = Color(0xFF42474B)
