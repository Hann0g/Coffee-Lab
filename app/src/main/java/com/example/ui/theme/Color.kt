package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemeColor(
    val id: String,
    val displayName: String,
    val description: String,
    val previewColor: Color,
    val secondaryPreview: Color
) {
    MILKY_COFFEE(
        id = "milky_coffee",
        displayName = "Milky Coffee",
        description = "Warm latte & creamy mocha",
        previewColor = Color(0xFF704828),
        secondaryPreview = Color(0xFFF3E7DC)
    ),
    MATCHA_GREEN(
        id = "matcha_green",
        displayName = "Matcha Green",
        description = "Fresh green tea & botanical notes",
        previewColor = Color(0xFF2E6B34),
        secondaryPreview = Color(0xFFD8ECD8)
    ),
    ESPRESSO_AMBER(
        id = "espresso_amber",
        displayName = "Espresso Amber",
        description = "Deep dark roast & warm caramel",
        previewColor = Color(0xFF8F4314),
        secondaryPreview = Color(0xFFFCE6D6)
    ),
    BERRY_ROASTER(
        id = "berry_roaster",
        displayName = "Berry Roaster",
        description = "Fruity origin notes & wild hibiscus",
        previewColor = Color(0xFF8E2A59),
        secondaryPreview = Color(0xFFFDD9E8)
    ),
    NORDIC_SLATE(
        id = "nordic_slate",
        displayName = "Nordic Slate",
        description = "Cold brew & Scandinavian slate",
        previewColor = Color(0xFF285D7C),
        secondaryPreview = Color(0xFFDAEBF5)
    );

    companion object {
        fun fromId(id: String?): AppThemeColor {
            return entries.find { it.id == id } ?: MILKY_COFFEE
        }
    }
}

// 1. Milky Coffee Theme Palette
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

// 2. Matcha Green Theme Palette
val MatchaPrimary = Color(0xFF2E6B34)
val MatchaOnPrimary = Color(0xFFFFFFFF)
val MatchaPrimaryContainer = Color(0xFFD8ECD8)
val MatchaOnPrimaryContainer = Color(0xFF0C2B10)
val MatchaSecondary = Color(0xFF4C7E52)
val MatchaOnSecondary = Color(0xFFFFFFFF)
val MatchaSecondaryContainer = Color(0xFFDEEBDF)
val MatchaOnSecondaryContainer = Color(0xFF192C1D)
val MatchaTertiary = Color(0xFF388550)
val MatchaOnTertiary = Color(0xFFFFFFFF)
val MatchaBackgroundLight = Color(0xFFF6FAF6)
val MatchaOnBackgroundLight = Color(0xFF182219)
val MatchaSurfaceLight = Color(0xFFFFFFFF)
val MatchaOnSurfaceLight = Color(0xFF182219)
val MatchaSurfaceVariantLight = Color(0xFFE5EFE6)
val MatchaOnSurfaceVariantLight = Color(0xFF435245)
val MatchaOutlineLight = Color(0xFFC4D5C6)
val MatchaOutlineVariantLight = Color(0xFFDDE7DF)

val MatchaPrimaryDark = Color(0xFF9FD6A4)
val MatchaOnPrimaryDark = Color(0xFF003911)
val MatchaPrimaryContainerDark = Color(0xFF16511E)
val MatchaOnPrimaryContainerDark = Color(0xFFD8ECD8)
val MatchaSecondaryDark = Color(0xFFBBDCBF)
val MatchaOnSecondaryDark = Color(0xFF203723)
val MatchaSecondaryContainerDark = Color(0xFF354E38)
val MatchaOnSecondaryContainerDark = Color(0xFFD8E8DA)
val MatchaBackgroundDark = Color(0xFF111712)
val MatchaOnBackgroundDark = Color(0xFFE2EBE3)
val MatchaSurfaceDark = Color(0xFF182219)
val MatchaOnSurfaceDark = Color(0xFFE2EBE3)
val MatchaSurfaceVariantDark = Color(0xFF2C392E)
val MatchaOnSurfaceVariantDark = Color(0xFFC4D5C6)
val MatchaOutlineDark = Color(0xFF869B89)
val MatchaOutlineVariantDark = Color(0xFF435245)

// 3. Espresso Amber Theme Palette
val EspressoPrimary = Color(0xFF8F4314)
val EspressoOnPrimary = Color(0xFFFFFFFF)
val EspressoPrimaryContainer = Color(0xFFFCE6D6)
val EspressoOnPrimaryContainer = Color(0xFF331402)
val EspressoSecondary = Color(0xFFA36338)
val EspressoOnSecondary = Color(0xFFFFFFFF)
val EspressoSecondaryContainer = Color(0xFFF7E2D2)
val EspressoOnSecondaryContainer = Color(0xFF372011)
val EspressoTertiary = Color(0xFFB55D28)
val EspressoOnTertiary = Color(0xFFFFFFFF)
val EspressoBackgroundLight = Color(0xFFFDF8F4)
val EspressoOnBackgroundLight = Color(0xFF251B15)
val EspressoSurfaceLight = Color(0xFFFFFFFF)
val EspressoOnSurfaceLight = Color(0xFF251B15)
val EspressoSurfaceVariantLight = Color(0xFFF4ECE4)
val EspressoOnSurfaceVariantLight = Color(0xFF564438)
val EspressoOutlineLight = Color(0xFFDAC6B8)
val EspressoOutlineVariantLight = Color(0xFFEBDFD5)

val EspressoPrimaryDark = Color(0xFFF3B78E)
val EspressoOnPrimaryDark = Color(0xFF522304)
val EspressoPrimaryContainerDark = Color(0xFF6F3007)
val EspressoOnPrimaryContainerDark = Color(0xFFFCE6D6)
val EspressoSecondaryDark = Color(0xFFDFC0A7)
val EspressoOnSecondaryDark = Color(0xFF402616)
val EspressoSecondaryContainerDark = Color(0xFF5A3C29)
val EspressoOnSecondaryContainerDark = Color(0xFFF4E3D5)
val EspressoBackgroundDark = Color(0xFF1A130E)
val EspressoOnBackgroundDark = Color(0xFFEFE6DF)
val EspressoSurfaceDark = Color(0xFF251B15)
val EspressoOnSurfaceDark = Color(0xFFEFE6DF)
val EspressoSurfaceVariantDark = Color(0xFF3F3127)
val EspressoOnSurfaceVariantDark = Color(0xFFDAC6B8)
val EspressoOutlineDark = Color(0xFFA08B7D)
val EspressoOutlineVariantDark = Color(0xFF564438)

// 4. Berry Roaster Theme Palette
val BerryPrimary = Color(0xFF8E2A59)
val BerryOnPrimary = Color(0xFFFFFFFF)
val BerryPrimaryContainer = Color(0xFFFDD9E8)
val BerryOnPrimaryContainer = Color(0xFF3B0520)
val BerrySecondary = Color(0xFFA34975)
val BerryOnSecondary = Color(0xFFFFFFFF)
val BerrySecondaryContainer = Color(0xFFF7DDE8)
val BerryOnSecondaryContainer = Color(0xFF3B1426)
val BerryTertiary = Color(0xFFAC3B6B)
val BerryOnTertiary = Color(0xFFFFFFFF)
val BerryBackgroundLight = Color(0xFFFCF6F9)
val BerryOnBackgroundLight = Color(0xFF24161E)
val BerrySurfaceLight = Color(0xFFFFFFFF)
val BerryOnSurfaceLight = Color(0xFF24161E)
val BerrySurfaceVariantLight = Color(0xFFF3E7ED)
val BerryOnSurfaceVariantLight = Color(0xFF56424C)
val BerryOutlineLight = Color(0xFFDAC3CE)
val BerryOutlineVariantLight = Color(0xFFEBDDE3)

val BerryPrimaryDark = Color(0xFFF4A8CA)
val BerryOnPrimaryDark = Color(0xFF54002F)
val BerryPrimaryContainerDark = Color(0xFF70123F)
val BerryOnPrimaryContainerDark = Color(0xFFFDD9E8)
val BerrySecondaryDark = Color(0xFFDEB5C8)
val BerryOnSecondaryDark = Color(0xFF431F30)
val BerrySecondaryContainerDark = Color(0xFF5D3346)
val BerryOnSecondaryContainerDark = Color(0xFFF6E1EC)
val BerryBackgroundDark = Color(0xFF1A0E15)
val BerryOnBackgroundDark = Color(0xFFEFE3E8)
val BerrySurfaceDark = Color(0xFF24161E)
val BerryOnSurfaceDark = Color(0xFFEFE3E8)
val BerrySurfaceVariantDark = Color(0xFF3E2D36)
val BerryOnSurfaceVariantDark = Color(0xFFDAC3CE)
val BerryOutlineDark = Color(0xFFA18895)
val BerryOutlineVariantDark = Color(0xFF56424C)

// 5. Nordic Slate Theme Palette
val NordicPrimary = Color(0xFF285D7C)
val NordicOnPrimary = Color(0xFFFFFFFF)
val NordicPrimaryContainer = Color(0xFFDAEBF5)
val NordicOnPrimaryContainer = Color(0xFF042436)
val NordicSecondary = Color(0xFF4A7B9B)
val NordicOnSecondary = Color(0xFFFFFFFF)
val NordicSecondaryContainer = Color(0xFFDEEBF2)
val NordicOnSecondaryContainer = Color(0xFF122835)
val NordicTertiary = Color(0xFF387299)
val NordicOnTertiary = Color(0xFFFFFFFF)
val NordicBackgroundLight = Color(0xFFF4F8FA)
val NordicOnBackgroundLight = Color(0xFF161E23)
val NordicSurfaceLight = Color(0xFFFFFFFF)
val NordicOnSurfaceLight = Color(0xFF161E23)
val NordicSurfaceVariantLight = Color(0xFFE3EDF2)
val NordicOnSurfaceVariantLight = Color(0xFF3F4F57)
val NordicOutlineLight = Color(0xFFC0D2DC)
val NordicOutlineVariantLight = Color(0xFFDAE5EB)

val NordicPrimaryDark = Color(0xFF9BCEEB)
val NordicOnPrimaryDark = Color(0xFF00344D)
val NordicPrimaryContainerDark = Color(0xFF0E4362)
val NordicOnPrimaryContainerDark = Color(0xFFDAEBF5)
val NordicSecondaryDark = Color(0xFFB4D3E5)
val NordicOnSecondaryDark = Color(0xFF1C3442)
val NordicSecondaryContainerDark = Color(0xFF324C5B)
val NordicOnSecondaryContainerDark = Color(0xFFD6E7F1)
val NordicBackgroundDark = Color(0xFF0E151A)
val NordicOnBackgroundDark = Color(0xFFE1EAF0)
val NordicSurfaceDark = Color(0xFF161E23)
val NordicOnSurfaceDark = Color(0xFFE1EAF0)
val NordicSurfaceVariantDark = Color(0xFF29363E)
val NordicOnSurfaceVariantDark = Color(0xFFC0D2DC)
val NordicOutlineDark = Color(0xFF8398A3)
val NordicOutlineVariantDark = Color(0xFF3F4F57)
