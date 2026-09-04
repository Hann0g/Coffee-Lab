package com.example.ui.model

data class PourPhase(
    val stageNumber: Int,
    val totalStages: Int,
    val name: String, // "Bloom", "First Pour", "Second Pour", "Drawdown & Finish"
    val startSeconds: Int,
    val endSeconds: Int,
    val targetWaterGrams: Double, // cumulative grams on scale
    val pourAmountGrams: Double, // grams to pour in this stage
    val instruction: String,
    val tip: String = ""
) {
    val durationSeconds: Int get() = (endSeconds - startSeconds).coerceAtLeast(1)
}

object PourScheduleCalculator {
    fun calculatePhases(
        method: String,
        coffeeGrams: Double,
        waterGrams: Double,
        targetTimeSeconds: Int
    ): List<PourPhase> {
        val normalizedMethod = method.trim().lowercase()
        return when {
            normalizedMethod.contains("espresso") -> calculateEspressoPhases(waterGrams, targetTimeSeconds)
            normalizedMethod.contains("aeropress") -> calculateAeroPressPhases(coffeeGrams, waterGrams, targetTimeSeconds)
            normalizedMethod.contains("french") || normalizedMethod.contains("immersion") ->
                calculateFrenchPressPhases(waterGrams, targetTimeSeconds)
            normalizedMethod.contains("cold brew") -> calculateColdBrewPhases(waterGrams, targetTimeSeconds)
            else -> calculatePourOverPhases(coffeeGrams, waterGrams, targetTimeSeconds)
        }
    }

    private fun calculatePourOverPhases(
        coffeeGrams: Double,
        waterGrams: Double,
        targetTimeSeconds: Int
    ): List<PourPhase> {
        val totalSec = targetTimeSeconds.coerceAtLeast(150)
        // Bloom is ~3x coffee weight or ~20% of water (e.g. 45g for 15g coffee)
        val bloomGrams = (coffeeGrams * 3.0).coerceIn(30.0, (waterGrams * 0.25).coerceAtLeast(35.0))
            .let { Math.round(it * 10.0) / 10.0 }
        val bloomEnd = 45

        // Phase 2: First pour to ~60% of total water (e.g. 150g for 250g)
        val firstPourTarget = Math.round(waterGrams * 0.60 * 10.0) / 10.0
        val firstPourAmount = Math.max(10.0, Math.round((firstPourTarget - bloomGrams) * 10.0) / 10.0)
        val firstPourEnd = 75 // 1:15

        // Phase 3: Second pour to 100% of total water (e.g. 250g)
        val secondPourAmount = Math.max(10.0, Math.round((waterGrams - firstPourTarget) * 10.0) / 10.0)
        val secondPourEnd = 110 // 1:50

        // Phase 4: Drawdown & Finish
        val drawdownEnd = totalSec

        return listOf(
            PourPhase(
                stageNumber = 1,
                totalStages = 4,
                name = "Bloom & Degas",
                startSeconds = 0,
                endSeconds = bloomEnd,
                targetWaterGrams = bloomGrams,
                pourAmountGrams = bloomGrams,
                instruction = "Pour ${bloomGrams.toInt()}g in gentle spirals. Swirl brewer lightly to saturate grounds.",
                tip = "Releases trapped CO2 for cleaner flavor and optimal extraction."
            ),
            PourPhase(
                stageNumber = 2,
                totalStages = 4,
                name = "First Pour",
                startSeconds = bloomEnd,
                endSeconds = firstPourEnd,
                targetWaterGrams = firstPourTarget,
                pourAmountGrams = firstPourAmount,
                instruction = "Pour steadily in outward spirals to ${firstPourTarget.toInt()}g (+${firstPourAmount.toInt()}g).",
                tip = "Pour from the center outwards, avoiding pouring directly onto filter paper."
            ),
            PourPhase(
                stageNumber = 3,
                totalStages = 4,
                name = "Second Pour",
                startSeconds = firstPourEnd,
                endSeconds = secondPourEnd,
                targetWaterGrams = waterGrams,
                pourAmountGrams = secondPourAmount,
                instruction = "Pour evenly to final target of ${waterGrams.toInt()}g (+${secondPourAmount.toInt()}g).",
                tip = "Maintain a steady, gentle kettle flow to keep slurry temperature stable."
            ),
            PourPhase(
                stageNumber = 4,
                totalStages = 4,
                name = "Drawdown & Finish",
                startSeconds = secondPourEnd,
                endSeconds = drawdownEnd,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Give a gentle final swirl. Allow coffee bed to draw down completely.",
                tip = "Observe bed flatness when drainage finishes. Ideal bed is level with uniform color."
            )
        )
    }

    private fun calculateEspressoPhases(yieldGrams: Double, targetTimeSeconds: Int): List<PourPhase> {
        val totalSec = targetTimeSeconds.coerceIn(20, 45)
        val preInfusionEnd = 5
        val mainExtractionEnd = totalSec
        val preTarget = Math.round((yieldGrams * 0.15) * 10.0) / 10.0
        return listOf(
            PourPhase(
                stageNumber = 1,
                totalStages = 3,
                name = "Pre-Infusion",
                startSeconds = 0,
                endSeconds = preInfusionEnd,
                targetWaterGrams = preTarget,
                pourAmountGrams = preTarget,
                instruction = "Gentle low-pressure wetting. Watch for initial espresso drops forming.",
                tip = "Saturates puck evenly to prevent high-pressure channeling."
            ),
            PourPhase(
                stageNumber = 2,
                totalStages = 3,
                name = "Full Pressure Extraction",
                startSeconds = preInfusionEnd,
                endSeconds = mainExtractionEnd,
                targetWaterGrams = yieldGrams,
                pourAmountGrams = yieldGrams - preTarget,
                instruction = "9-bar extraction. Watch for rich tiger-striped hazelnut crema.",
                tip = "Stop shot right when stream begins to blond and reaches ${yieldGrams.toInt()}g yield."
            ),
            PourPhase(
                stageNumber = 3,
                totalStages = 3,
                name = "Shot Complete",
                startSeconds = mainExtractionEnd,
                endSeconds = mainExtractionEnd + 10,
                targetWaterGrams = yieldGrams,
                pourAmountGrams = 0.0,
                instruction = "Disengage group head. Stir espresso with a spoon before tasting.",
                tip = "Stirring combines dense bottom extraction with aromatic top crema."
            )
        )
    }

    private fun calculateAeroPressPhases(coffeeGrams: Double, waterGrams: Double, targetTimeSeconds: Int): List<PourPhase> {
        val totalSec = targetTimeSeconds.coerceAtLeast(90)
        return listOf(
            PourPhase(
                stageNumber = 1,
                totalStages = 3,
                name = "Pour & Stir",
                startSeconds = 0,
                endSeconds = 20,
                targetWaterGrams = waterGrams,
                pourAmountGrams = waterGrams,
                instruction = "Pour all ${waterGrams.toInt()}g hot water quickly. Stir gently 3 times.",
                tip = "Insert plunger slightly to create a vacuum seal and prevent dripping."
            ),
            PourPhase(
                stageNumber = 2,
                totalStages = 3,
                name = "Steep Time",
                startSeconds = 20,
                endSeconds = totalSec - 30,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Let coffee steep undisturbed to develop full body and sweet notes.",
                tip = "Gentle swirl at 1 minute to break floating crust."
            ),
            PourPhase(
                stageNumber = 3,
                totalStages = 3,
                name = "Gentle Plunge",
                startSeconds = totalSec - 30,
                endSeconds = totalSec,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Press plunger down slowly with body weight for 30s until hissing sound.",
                tip = "Stop pressing when the hiss begins to prevent bitter tannins from entering the cup."
            )
        )
    }

    private fun calculateFrenchPressPhases(waterGrams: Double, targetTimeSeconds: Int): List<PourPhase> {
        val totalSec = targetTimeSeconds.coerceAtLeast(240)
        return listOf(
            PourPhase(
                stageNumber = 1,
                totalStages = 4,
                name = "Pour & Saturate",
                startSeconds = 0,
                endSeconds = 45,
                targetWaterGrams = waterGrams,
                pourAmountGrams = waterGrams,
                instruction = "Pour all ${waterGrams.toInt()}g boiling water vigorously over grounds.",
                tip = "Ensure all dry coffee is submerged under water. Place lid on top without pressing."
            ),
            PourPhase(
                stageNumber = 2,
                totalStages = 4,
                name = "Main Steep",
                startSeconds = 45,
                endSeconds = 240,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Allow coffee to steep for 4 minutes completely undisturbed.",
                tip = "Allows natural immersion extraction of origin oils and sugars."
            ),
            PourPhase(
                stageNumber = 3,
                totalStages = 4,
                name = "Break Crust & Skim",
                startSeconds = 240,
                endSeconds = 300,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Stir surface crust gently. Scoop out floating foam and remaining particles with spoons.",
                tip = "Hoffmann technique: Skimming foam removes bitter particulate for a clean cup."
            ),
            PourPhase(
                stageNumber = 4,
                totalStages = 4,
                name = "Settle & Pour",
                startSeconds = 300,
                endSeconds = totalSec,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Insert plunger as a strainer without pressing down to the bottom. Pour gently.",
                tip = "Pour slowly to keep sediment undisturbed at the bottom of the press."
            )
        )
    }

    private fun calculateColdBrewPhases(waterGrams: Double, targetTimeSeconds: Int): List<PourPhase> {
        return listOf(
            PourPhase(
                stageNumber = 1,
                totalStages = 2,
                name = "Infusion & Agitation",
                startSeconds = 0,
                endSeconds = 60,
                targetWaterGrams = waterGrams,
                pourAmountGrams = waterGrams,
                instruction = "Combine coarse coffee grounds with ${waterGrams.toInt()}g cold filtered water. Stir well.",
                tip = "Ensure no dry clumps remain at the bottom of the jar."
            ),
            PourPhase(
                stageNumber = 2,
                totalStages = 2,
                name = "Slow Cold Steep",
                startSeconds = 60,
                endSeconds = targetTimeSeconds,
                targetWaterGrams = waterGrams,
                pourAmountGrams = 0.0,
                instruction = "Seal container and place in refrigerator for 12–16 hours.",
                tip = "Filter through a paper filter for a crystalline, sweet concentrate."
            )
        )
    }
}
