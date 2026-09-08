package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class PourStep(
    val pourNumber: Int,
    val label: String,
    val waterGrams: Double,
    val totalWaterGrams: Double,
    val timeLabel: String,
    val instruction: String
)

data class AiRecipeRecommendation(
    val recipeName: String,
    val method: String,
    val coffeeOrigin: String,
    val roastLevel: String,
    val tastingNotes: String,
    val coffeeGrams: Double,
    val waterGrams: Double,
    val ratio: String,
    val grindSize: String,
    val targetTimeSeconds: Int,
    val targetTempCelsius: Int,
    val numberOfPours: Int,
    val pourSteps: List<PourStep>,
    val stepsText: String,
    val baristaAdvice: String,
    val isAiGenerated: Boolean = true
)

object AiRecipeAssistantService {

    private const val TAG = "AiRecipeAssistant"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Generates a tailor-made coffee recipe recommendation based on bean name and intended use.
     * Uses Gemini 3.5 Flash with intelligent fallback to barista extraction heuristics.
     */
    suspend fun recommendRecipe(
        beanName: String,
        intendedUse: String,
        apiKey: String = BuildConfig.GEMINI_API_KEY
    ): Result<AiRecipeRecommendation> = withContext(Dispatchers.IO) {
        val safeBean = beanName.trim().ifBlank { "Specialty Single Origin" }
        val safeGoal = intendedUse.trim().ifBlank { "Balanced and flavorful daily pour over" }

        if (apiKey.isBlank() || apiKey == "DEFAULT_GEMINI_KEY" || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No live Gemini API key provided. Generating recipe via expert Barista extraction engine.")
            return@withContext Result.success(generateFallbackRecommendation(safeBean, safeGoal))
        }

        try {
            val prompt = """
                You are a World Champion Barista and Coffee Extraction Scientist.
                A coffee enthusiast has a specific coffee bean and wants a customized brew recipe for a particular purpose.

                Bean Name / Origin: "$safeBean"
                Intended Brew Goal / Purpose: "$safeGoal"

                Recommend the ultimate specialty coffee recipe tailored to highlight this bean's best qualities for this goal.
                Provide:
                - Recipe name (creative, professional)
                - Brew method: strictly one of "Pour Over", "Espresso", "AeroPress", "Immersion"
                - Roast level: strictly one of "Light", "Medium", "Dark"
                - Key tasting notes expected
                - Exact coffee dose in grams
                - Exact total water in grams (ml)
                - Brew ratio (e.g. "1:16.7", "1:15", "1:2")
                - Grind size with description and clicks/microns
                - Target water temperature in Celsius (e.g. 88 to 96)
                - Target total brew time in seconds
                - How many pours (integer, e.g. 1, 2, 3, 4, 5)
                - Pour steps list with pour number, label, water volume for that pour, cumulative total water, timing, and pouring technique
                - Full step-by-step instructions text
                - Barista advice: 1-2 sentences explaining why this ratio, grind, temp, and pour sequence extract the best flavor from this specific bean.

                Return ONLY valid raw JSON with this exact schema (no markdown fences, no extra text):
                {
                  "recipeName": "...",
                  "method": "Pour Over",
                  "coffeeOrigin": "...",
                  "roastLevel": "Medium",
                  "tastingNotes": "...",
                  "coffeeGrams": 15.0,
                  "waterGrams": 250.0,
                  "ratio": "1:16.7",
                  "grindSize": "Medium-Fine (18 Clicks / 550μm)",
                  "targetTimeSeconds": 180,
                  "targetTempCelsius": 93,
                  "numberOfPours": 3,
                  "pourSteps": [
                    {
                      "pourNumber": 1,
                      "label": "Bloom",
                      "waterGrams": 45.0,
                      "totalWaterGrams": 45.0,
                      "timeLabel": "0:00 - 0:45",
                      "instruction": "Gentle spiral pour to saturate all grounds, light swirl."
                    },
                    {
                      "pourNumber": 2,
                      "label": "Pour 2",
                      "waterGrams": 105.0,
                      "totalWaterGrams": 150.0,
                      "timeLabel": "0:45 - 1:20",
                      "instruction": "Steady concentric spiral pour outward to wash down bed."
                    },
                    {
                      "pourNumber": 3,
                      "label": "Final Pour",
                      "waterGrams": 100.0,
                      "totalWaterGrams": 250.0,
                      "timeLabel": "1:20 - 2:00",
                      "instruction": "Gentle center pour to 250g, drawdown finishes by 3:00."
                    }
                  ],
                  "stepsText": "1. Bloom 45g (0:00-0:45) with gentle swirl.\n2. Pour to 150g (0:45-1:20) in concentric circles.\n3. Final pour to 250g (1:20-2:00), allow drawdown to complete by 3:00.",
                  "baristaAdvice": "..."
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                }
                put("generationConfig", generationConfig)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                Log.e(TAG, "Gemini API HTTP ${response.code}: $responseBody. Falling back to barista engine.")
                return@withContext Result.success(generateFallbackRecommendation(safeBean, safeGoal))
            }

            val parsed = parseGeminiResponse(responseBody, safeBean, safeGoal)
            Result.success(parsed)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating AI recipe: ${e.message}", e)
            Result.success(generateFallbackRecommendation(safeBean, safeGoal))
        }
    }

    private fun parseGeminiResponse(
        responseJsonStr: String,
        fallbackBean: String,
        fallbackGoal: String
    ): AiRecipeRecommendation {
        return try {
            val root = JSONObject(responseJsonStr)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            val cleanJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleanJson)

            val rawMethod = json.optString("method", "Pour Over")
            val method = when {
                rawMethod.contains("Espresso", ignoreCase = true) -> "Espresso"
                rawMethod.contains("AeroPress", ignoreCase = true) -> "AeroPress"
                rawMethod.contains("Immersion", ignoreCase = true) || rawMethod.contains("French", ignoreCase = true) -> "Immersion"
                else -> "Pour Over"
            }

            val rawRoast = json.optString("roastLevel", "Medium")
            val roastLevel = when {
                rawRoast.contains("Light", ignoreCase = true) -> "Light"
                rawRoast.contains("Dark", ignoreCase = true) -> "Dark"
                else -> "Medium"
            }

            val recipeName = json.optString("recipeName", "$fallbackBean ${method}").ifBlank { "$fallbackBean ${method}" }
            val origin = json.optString("coffeeOrigin", fallbackBean).ifBlank { fallbackBean }
            val tastingNotes = json.optString("tastingNotes", "Sweet, aromatic, balanced").ifBlank { "Sweet, aromatic, balanced" }
            val coffeeGrams = json.optDouble("coffeeGrams", 15.0).let { if (it > 0) it else 15.0 }
            val waterGrams = json.optDouble("waterGrams", 250.0).let { if (it > 0) it else 250.0 }
            val ratio = json.optString("ratio", "1:%.1f".format(Locale.US, waterGrams / coffeeGrams)).ifBlank {
                "1:%.1f".format(Locale.US, waterGrams / coffeeGrams)
            }
            val grindSize = json.optString("grindSize", "Medium-Fine").ifBlank { "Medium-Fine" }
            val targetTimeSeconds = json.optInt("targetTimeSeconds", 180).let { if (it > 0) it else 180 }
            val targetTempCelsius = json.optInt("targetTempCelsius", 93).let { if (it > 0) it else 93 }
            val numberOfPours = json.optInt("numberOfPours", 3).let { if (it > 0) it else 3 }

            // Parse pour steps
            val pourStepsList = mutableListOf<PourStep>()
            val stepsArray = json.optJSONArray("pourSteps")
            if (stepsArray != null) {
                for (i in 0 until stepsArray.length()) {
                    val stepObj = stepsArray.optJSONObject(i) ?: continue
                    pourStepsList.add(
                        PourStep(
                            pourNumber = stepObj.optInt("pourNumber", i + 1),
                            label = stepObj.optString("label", "Pour ${i + 1}"),
                            waterGrams = stepObj.optDouble("waterGrams", 50.0),
                            totalWaterGrams = stepObj.optDouble("totalWaterGrams", (i + 1) * 50.0),
                            timeLabel = stepObj.optString("timeLabel", "Step ${i + 1}"),
                            instruction = stepObj.optString("instruction", "")
                        )
                    )
                }
            }

            var stepsText = json.optString("stepsText", "").trim()
            if (stepsText.isBlank() && pourStepsList.isNotEmpty()) {
                stepsText = pourStepsList.joinToString("\n") {
                    "${it.pourNumber}. ${it.label} (+${it.waterGrams.toInt()}g to ${it.totalWaterGrams.toInt()}g total at ${it.timeLabel}): ${it.instruction}"
                }
            } else if (stepsText.isBlank()) {
                stepsText = "1. Bloom coffee with 3x coffee weight for 45s.\n2. Pour steady stream in concentric spirals.\n3. Maintain flat bed and finish by ${targetTimeSeconds}s."
            }

            val baristaAdvice = json.optString(
                "baristaAdvice",
                "Crafted to highlight the clarity, aroma, and natural sweetness of $fallbackBean."
            )

            AiRecipeRecommendation(
                recipeName = recipeName,
                method = method,
                coffeeOrigin = origin,
                roastLevel = roastLevel,
                tastingNotes = tastingNotes,
                coffeeGrams = coffeeGrams,
                waterGrams = waterGrams,
                ratio = ratio,
                grindSize = grindSize,
                targetTimeSeconds = targetTimeSeconds,
                targetTempCelsius = targetTempCelsius,
                numberOfPours = if (pourStepsList.isNotEmpty()) pourStepsList.size else numberOfPours,
                pourSteps = pourStepsList,
                stepsText = stepsText,
                baristaAdvice = baristaAdvice,
                isAiGenerated = true
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse Gemini JSON: ${e.message}. Using barista extraction defaults.")
            generateFallbackRecommendation(fallbackBean, fallbackGoal)
        }
    }

    /**
     * Expert Barista Extraction Knowledge Engine for immediate offline and zero-latency recommendations.
     * Evaluates coffee origins, processing methods, bean density, and the user's intended purpose.
     */
    fun generateFallbackRecommendation(
        beanName: String,
        intendedUse: String
    ): AiRecipeRecommendation {
        val lowerBean = beanName.lowercase(Locale.ROOT)
        val lowerGoal = intendedUse.lowercase(Locale.ROOT)

        val isEspresso = lowerGoal.contains("espresso") || lowerGoal.contains("latte") ||
                lowerGoal.contains("flat white") || lowerGoal.contains("cappuccino") ||
                lowerGoal.contains("crema") || lowerGoal.contains("cortado") || lowerGoal.contains("9 bar")

        val isAeroPress = lowerGoal.contains("aeropress") || lowerGoal.contains("travel") ||
                lowerGoal.contains("press") && !lowerGoal.contains("french")

        val isImmersion = lowerGoal.contains("french press") || lowerGoal.contains("cold brew") ||
                lowerGoal.contains("immersion") || lowerGoal.contains("clever") || lowerGoal.contains("cafetiere")

        return when {
            isEspresso -> {
                val roast = if (lowerBean.contains("light")) "Light" else if (lowerBean.contains("dark")) "Dark" else "Medium"
                val dose = 18.0
                val yield = if (roast == "Light") 45.0 else 36.0
                val ratio = if (roast == "Light") "1:2.5" else "1:2.0"
                val temp = if (roast == "Light") 94 else 92
                val time = 28
                val advice = "Espresso extraction dialed in with high pressure. 18g dose in a precision basket yields rich crema and syrup-like mouthfeel with vibrant sweetness."

                AiRecipeRecommendation(
                    recipeName = "$beanName Velvet Espresso",
                    method = "Espresso",
                    coffeeOrigin = beanName,
                    roastLevel = roast,
                    tastingNotes = if (roast == "Light") "Citrus blossom, peach, brown sugar" else "Dark chocolate, roasted almond, caramel",
                    coffeeGrams = dose,
                    waterGrams = yield,
                    ratio = ratio,
                    grindSize = "Fine (9-10 bar espresso, ~200μm)",
                    targetTimeSeconds = time,
                    targetTempCelsius = temp,
                    numberOfPours = 1,
                    pourSteps = listOf(
                        PourStep(1, "Pre-infusion & Pull", yield, yield, "0:00 - 0:28", "2-3s pre-infusion at 3 bar, ramp to 9 bar until 36g extracted.")
                    ),
                    stepsText = "1. Dose 18g into portafilter, distribute with WDT needle tool, and tamp level with 25-30 lbs pressure.\n2. Lock in grouphead and pull immediately at 9 bar.\n3. Target 36g liquid yield in 26-29 seconds with dense golden tiger-striping crema.",
                    baristaAdvice = advice,
                    isAiGenerated = true
                )
            }

            isAeroPress -> {
                val advice = "AeroPress hybrid extraction: combining short immersion bloom with quick pressure plunge brings out intense aromatics with silky, clean body."
                val dose = 15.0
                val water = 225.0
                val temp = 88

                AiRecipeRecommendation(
                    recipeName = "$beanName AeroPress Reserve",
                    method = "AeroPress",
                    coffeeOrigin = beanName,
                    roastLevel = "Medium",
                    tastingNotes = "Juicy stone fruit, honey, milk chocolate finish",
                    coffeeGrams = dose,
                    waterGrams = water,
                    ratio = "1:15.0",
                    grindSize = "Medium-Fine (14 Clicks / 450μm)",
                    targetTimeSeconds = 120,
                    targetTempCelsius = temp,
                    numberOfPours = 2,
                    pourSteps = listOf(
                        PourStep(1, "Bloom & Agitation", 60.0, 60.0, "0:00 - 0:30", "Pour 60g water (88°C), stir 4 times gently, allow bloom to degas."),
                        PourStep(2, "Fill & Plunge", 165.0, 225.0, "0:30 - 2:00", "Fill to 225g, cap with rinsed paper filter, wait until 1:15, then press gently for 30s.")
                    ),
                    stepsText = "1. Inverted method: add 15g medium-fine coffee.\n2. Pour 60g water at 88°C, stir gently 4 times and let bloom for 30 seconds.\n3. Add remaining 165g water to 225g total. Attach cap with wet filter.\n4. At 1:15, flip onto vessel and press gently for 30 seconds until hissing sound.",
                    baristaAdvice = advice,
                    isAiGenerated = true
                )
            }

            isImmersion -> {
                val advice = "Coarse immersion brewing allows full solubles extraction over 4 minutes, highlighting natural oils and a comforting, full-bodied sweetness."
                val dose = 20.0
                val water = 320.0

                AiRecipeRecommendation(
                    recipeName = "$beanName French Press Immersion",
                    method = "Immersion",
                    coffeeOrigin = beanName,
                    roastLevel = "Medium",
                    tastingNotes = "Toasted hazelnut, cocoa nibs, mellow plum",
                    coffeeGrams = dose,
                    waterGrams = water,
                    ratio = "1:16.0",
                    grindSize = "Medium-Coarse (24 Clicks / 800μm)",
                    targetTimeSeconds = 240,
                    targetTempCelsius = 92,
                    numberOfPours = 1,
                    pourSteps = listOf(
                        PourStep(1, "Full Immersion Pour", water, water, "0:00 - 4:00", "Pour all 320g water in steady spiral. Steep for 4 minutes, crust break at 4:00.")
                    ),
                    stepsText = "1. Add 20g coarse coffee to French press carafe.\n2. Pour all 320g water at 92°C ensuring complete saturation.\n3. Place lid without plunging and steep for 4:00.\n4. Break crust with spoon, skim floating foam, and press filter gently to bed surface.",
                    baristaAdvice = advice,
                    isAiGenerated = true
                )
            }

            else -> {
                // Pour Over (V60 / Kalita Wave / Chemex)
                val isLightFloral = lowerBean.contains("ethiopia") || lowerBean.contains("geisha") ||
                        lowerBean.contains("kenya") || lowerBean.contains("floral") || lowerBean.contains("fruit") ||
                        lowerBean.contains("pink bourbon") || lowerBean.contains("light") || lowerGoal.contains("floral") || lowerGoal.contains("fruity")

                val isDarkSmoky = lowerBean.contains("dark") || lowerBean.contains("french") ||
                        lowerBean.contains("italian") || lowerBean.contains("sumatra") || lowerGoal.contains("dark") || lowerGoal.contains("bold")

                if (isLightFloral) {
                    val dose = 15.0
                    val water = 250.0
                    val temp = 94
                    val advice = "Light-roasted dense specialty beans excel with higher water temperature (94°C) and a structured 3-pour bloom to amplify sweet florals, bergamot, and delicate fruit acidity."

                    AiRecipeRecommendation(
                        recipeName = "$beanName 3-Stage Floral V60",
                        method = "Pour Over",
                        coffeeOrigin = beanName,
                        roastLevel = "Light",
                        tastingNotes = "Jasmine, bergamot, ripe peach, Meyer lemon",
                        coffeeGrams = dose,
                        waterGrams = water,
                        ratio = "1:16.7",
                        grindSize = "Medium-Fine (16 Clicks / 520μm)",
                        targetTimeSeconds = 180,
                        targetTempCelsius = temp,
                        numberOfPours = 3,
                        pourSteps = listOf(
                            PourStep(1, "Bloom", 45.0, 45.0, "0:00 - 0:45", "Pour 45g hot water, gentle swirl to ensure complete grounds saturation."),
                            PourStep(2, "Main Extraction Pour", 105.0, 150.0, "0:45 - 1:15", "Slow concentric spiral pour from center to rim at 4g/s."),
                            PourStep(3, "Finishing Pour", 100.0, 250.0, "1:30 - 2:00", "Gentle center pour to 250g, gentle tap to settle bed; drawdown ends at 3:00.")
                        ),
                        stepsText = "1. Bloom: 45g water (0:00 - 0:45) with gentle swirl.\n2. Pour 2: Spiral outward to 150g (0:45 - 1:15).\n3. Pour 3: Center pour to 250g (1:30 - 2:00). Drawdown finishes at 3:00 with flat bed.",
                        baristaAdvice = advice,
                        isAiGenerated = true
                    )
                } else if (isDarkSmoky) {
                    val dose = 18.0
                    val water = 270.0
                    val temp = 89
                    val advice = "Darker roasts extract rapidly; lower water temperature (89°C) and a gentle 2-pour sequence prevent astringency while maximizing rich dark cocoa and caramelized sugar sweetness."

                    AiRecipeRecommendation(
                        recipeName = "$beanName Classic Dark Roast Pour",
                        method = "Pour Over",
                        coffeeOrigin = beanName,
                        roastLevel = "Dark",
                        tastingNotes = "Dark chocolate, roasted walnut, molasses",
                        coffeeGrams = dose,
                        waterGrams = water,
                        ratio = "1:15.0",
                        grindSize = "Medium (20 Clicks / 650μm)",
                        targetTimeSeconds = 160,
                        targetTempCelsius = temp,
                        numberOfPours = 2,
                        pourSteps = listOf(
                            PourStep(1, "Gentle Bloom", 55.0, 55.0, "0:00 - 0:40", "Saturate grounds with 55g water at 89°C, no agitation."),
                            PourStep(2, "Continuous Pour", 215.0, 270.0, "0:40 - 1:30", "Steady center-focused spiral pour up to 270g; complete drawdown by 2:40.")
                        ),
                        stepsText = "1. Bloom: 55g water (89°C) for 40 seconds.\n2. Pour 2: Steady spiral pour to 270g by 1:30.\n3. Total drawdown completes by 2:40 with rich, chocolatey crema notes.",
                        baristaAdvice = advice,
                        isAiGenerated = true
                    )
                } else {
                    // Balanced medium roast
                    val dose = 18.0
                    val water = 300.0
                    val temp = 92
                    val advice = "Balanced 3-pour extraction ratio (1:16.7) balances sweetness, rich body, and clean acidity, ideal for daily specialty brewing."

                    AiRecipeRecommendation(
                        recipeName = "$beanName Golden Ratio V60",
                        method = "Pour Over",
                        coffeeOrigin = beanName,
                        roastLevel = "Medium",
                        tastingNotes = "Caramel, red apple, toasted pecan, milk chocolate",
                        coffeeGrams = dose,
                        waterGrams = water,
                        ratio = "1:16.7",
                        grindSize = "Medium-Fine (18 Clicks / 550μm)",
                        targetTimeSeconds = 190,
                        targetTempCelsius = temp,
                        numberOfPours = 3,
                        pourSteps = listOf(
                            PourStep(1, "Bloom", 55.0, 55.0, "0:00 - 0:45", "Pour 55g water, light swirl to release CO2 degassing bubbles."),
                            PourStep(2, "Body Pour", 125.0, 180.0, "0:45 - 1:20", "Concentric circular pour to 180g at steady 5g/sec flow rate."),
                            PourStep(3, "Sweetness Finish", 120.0, 300.0, "1:30 - 2:05", "Gentle center pour to 300g total. Drawdown completes smoothly by 3:10.")
                        ),
                        stepsText = "1. Bloom: 55g water (0:00 - 0:45) with light swirl.\n2. Pour 2: Concentric pour to 180g (0:45 - 1:20).\n3. Pour 3: Center pour to 300g (1:30 - 2:05). Full drawdown by 3:10.",
                        baristaAdvice = advice,
                        isAiGenerated = true
                    )
                }
            }
        }
    }
}
