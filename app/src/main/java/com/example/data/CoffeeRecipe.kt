package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coffee_recipes")
data class CoffeeRecipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val method: String, // "Pour Over", "Espresso", "AeroPress", "Immersion", "Chemex", "Cold Brew", "Moka Pot"
    val coffeeOrigin: String = "", // e.g. "Ethiopia Yirgacheffe", "Colombia Huila"
    val roastLevel: String = "Medium", // Light, Medium, Dark, etc.
    val tastingNotes: String = "", // e.g. "Floral, Bergamot, Blueberry"
    val defaultCoffeeGrams: Double = 15.0,
    val defaultWaterGrams: Double = 250.0,
    val ratio: String = "1:16.7",
    val grindSize: String = "Medium-Fine", // "Fine", "Medium-Fine", "Medium", "Medium-Coarse", "Coarse"
    val targetTimeSeconds: Int = 180,
    val targetTempCelsius: Int = 93,
    val steps: String = "",
    val isPreset: Boolean = false
)

