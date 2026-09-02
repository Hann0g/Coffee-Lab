package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bean_bags")
data class BeanBag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val roaster: String = "",
    val roastLevel: String = "Medium", // Light, Medium-Light, Medium, Medium-Dark, Dark
    val roastDate: String = "",
    val initialWeightGrams: Double = 250.0,
    val remainingWeightGrams: Double = 250.0,
    val isActive: Boolean = true
)
