package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coffee_brews")
data class CoffeeBrew(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeName: String,
    val coffeeGrams: Double,
    val waterGrams: Double,
    val ratio: String = "1:16.7",
    val grindSize: String = "Medium",
    val brewTimeSeconds: Int = 180,
    val waterTempCelsius: Int = 93,
    val beanRoast: String = "House Blend",
    val rating: Int = 5, // 1 to 5 stars
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val bagId: Long? = null
)
