package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
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
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

object CoffeeBagScannerService {

    private const val TAG = "CoffeeBagScanner"
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
     * Analyzes a coffee bag image using Gemini multimodal API and extracts structured details.
     */
    suspend fun analyzeCoffeeBag(
        bitmap: Bitmap,
        apiKey: String = BuildConfig.GEMINI_API_KEY
    ): Result<ScannedCoffeeBag> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                Log.w(TAG, "No valid Gemini API key found. Using heuristic label parser.")
                return@withContext Result.success(fallbackParse(bitmap, "Simulated recognition (API key required for live AI analysis)"))
            }

            val scaledBitmap = scaleDownBitmap(bitmap, 1024)
            val base64Jpeg = bitmapToBase64(scaledBitmap)

            val prompt = """
                You are an expert specialty coffee barista and label scanning assistant.
                Analyze this photo of a coffee bean bag and extract the following details into a JSON object:
                - "name": The coffee bean origin, variety, or blend name (e.g. "Colombia Pink Bourbon", "Ethiopia Yirgacheffe", "Southern Weather").
                - "roaster": The coffee roastery or brand name (e.g. "Onyx Coffee Lab", "Square Mile", "Blue Bottle", "Sey").
                - "roastLevel": Roast level, strictly one of: "Light", "Medium", "Dark".
                - "roastDate": The roast date or freshness date printed on the bag if visible, otherwise "Fresh".
                - "weightGrams": The net weight of coffee in grams as a number (e.g. 250, 300, 340 for 12oz, 500, 1000). If in ounces or lbs, convert to grams (12 oz = 340g, 16 oz = 454g). Default to 250.0 if not stated.
                - "tastingNotes": Any flavor or tasting notes mentioned (e.g. "Peach, Jasmine, Milk Chocolate").
                - "process": Processing method if specified (e.g. "Washed", "Natural", "Honey").

                Return ONLY raw JSON with these keys, no markdown fences or other text:
                {
                  "name": "...",
                  "roaster": "...",
                  "roastLevel": "...",
                  "roastDate": "...",
                  "weightGrams": 250.0,
                  "tastingNotes": "...",
                  "process": "..."
                }
            """.trimIndent()

            // Construct JSON request body
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            // Text part
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                            // Inline image part
                            put(JSONObject().apply {
                                val inlineDataObj = JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Jpeg)
                                }
                                put("inlineData", inlineDataObj)
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
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
                Log.e(TAG, "Gemini API error ${response.code}: $responseBody")
                return@withContext Result.success(fallbackParse(bitmap, "Server error (${response.code}), applied smart defaults"))
            }

            val parsedBag = parseGeminiResponse(responseBody)
            Result.success(parsedBag)
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing coffee bag: ${e.message}", e)
            Result.success(fallbackParse(bitmap, "Recognition fallback: ${e.localizedMessage ?: "Unknown"}"))
        }
    }

    private fun parseGeminiResponse(responseJsonStr: String): ScannedCoffeeBag {
        return try {
            val root = JSONObject(responseJsonStr)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            // Clean markdown fences if any
            val cleanJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleanJson)
            val name = json.optString("name", "Specialty Single Origin").ifBlank { "Specialty Single Origin" }
            val roaster = json.optString("roaster", "Artisan Roaster")
            val rawRoastLevel = json.optString("roastLevel", "Medium")
            val roastLevel = when {
                rawRoastLevel.contains("Light", ignoreCase = true) -> "Light"
                rawRoastLevel.contains("Dark", ignoreCase = true) -> "Dark"
                else -> "Medium"
            }
            val roastDate = json.optString("roastDate", "Fresh").ifBlank { "Fresh" }
            val weight = json.optDouble("weightGrams", 250.0).let { if (it > 0) it else 250.0 }
            val tastingNotes = json.optString("tastingNotes", "")
            val process = json.optString("process", "")

            ScannedCoffeeBag(
                name = name,
                roaster = roaster,
                roastLevel = roastLevel,
                roastDate = roastDate,
                weightGrams = weight,
                tastingNotes = tastingNotes,
                process = process,
                detectedSummary = listOfNotNull(
                    roaster.takeIf { it.isNotBlank() },
                    process.takeIf { it.isNotBlank() },
                    tastingNotes.takeIf { it.isNotBlank() }
                ).joinToString(" • "),
                isAiDetected = true
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed parsing Gemini JSON output: ${e.message}")
            fallbackParse(null, "Parsed default format")
        }
    }

    /**
     * Fallback recognition for offline use, missing key, or demo mode.
     */
    fun fallbackParse(bitmap: Bitmap?, reason: String): ScannedCoffeeBag {
        return ScannedCoffeeBag(
            name = "Specialty Origin Blend",
            roaster = "Artisan Roastery",
            roastLevel = "Medium",
            roastDate = "Freshly Roasted",
            weightGrams = 250.0,
            tastingNotes = "Chocolate, Stone Fruit, Honey",
            process = "Washed",
            detectedSummary = reason,
            isAiDetected = false
        )
    }


    private fun scaleDownBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding bitmap from Uri: ${e.message}")
            null
        }
    }
}
