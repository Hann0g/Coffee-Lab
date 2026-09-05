package com.example.data

data class ScannedCoffeeBag(
    val name: String,
    val roaster: String = "",
    val roastLevel: String = "Medium", // Light, Medium, Dark
    val roastDate: String = "",
    val weightGrams: Double = 250.0,
    val tastingNotes: String = "",
    val process: String = "",
    val detectedSummary: String = "",
    val isAiDetected: Boolean = true
)
