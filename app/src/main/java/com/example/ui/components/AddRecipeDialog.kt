package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AiRecipeAssistantService
import com.example.data.AiRecipeRecommendation
import com.example.data.BeanBag
import com.example.ui.theme.PixelBadge
import com.example.ui.theme.PixelButton
import com.example.ui.theme.PixelDialogueBox
import com.example.ui.theme.PixelWindow
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRecipeDialog(
    beanBags: List<BeanBag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        method: String,
        coffeeOrigin: String,
        roastLevel: String,
        tastingNotes: String,
        coffeeGrams: Double,
        waterGrams: Double,
        ratio: String,
        grindSize: String,
        targetTimeSeconds: Int,
        targetTempCelsius: Int,
        steps: String
    ) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Form fields
    var name by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("Pour Over") }
    var coffeeOrigin by remember { mutableStateOf("") }
    var roastLevel by remember { mutableStateOf("Medium") }
    var tastingNotes by remember { mutableStateOf("") }
    var coffeeGramsStr by remember { mutableStateOf("15") }
    var waterGramsStr by remember { mutableStateOf("250") }
    var grindSize by remember { mutableStateOf("Medium-Fine") }
    var targetTimeSecStr by remember { mutableStateOf("180") }
    var targetTempStr by remember { mutableStateOf("93") }
    var steps by remember { mutableStateOf("") }

    // AI Assistant Inputs & State
    val defaultBagName = beanBags.firstOrNull { it.isActive }?.name ?: beanBags.firstOrNull()?.name ?: ""
    var aiBeanName by remember { mutableStateOf(defaultBagName) }
    var aiIntendedUse by remember { mutableStateOf("") }
    var isAiGenerating by remember { mutableStateOf(false) }
    var aiRecommendation by remember { mutableStateOf<AiRecipeRecommendation?>(null) }
    var aiStatusMessage by remember { mutableStateOf<String?>(null) }

    fun executeAiRecommendation() {
        val targetBean = aiBeanName.trim().ifBlank { "Specialty Coffee" }
        val targetGoal = aiIntendedUse.trim().ifBlank { "Balanced and flavorful pour over" }

        isAiGenerating = true
        aiStatusMessage = null

        scope.launch {
            val result = AiRecipeAssistantService.recommendRecipe(
                beanName = targetBean,
                intendedUse = targetGoal
            )

            val rec = result.getOrNull() ?: AiRecipeAssistantService.generateFallbackRecommendation(targetBean, targetGoal)

            // Auto-fill all recipe parameter fields
            name = rec.recipeName
            method = rec.method
            coffeeOrigin = rec.coffeeOrigin
            roastLevel = rec.roastLevel
            tastingNotes = rec.tastingNotes
            coffeeGramsStr = if (rec.coffeeGrams % 1.0 == 0.0) rec.coffeeGrams.toInt().toString() else rec.coffeeGrams.toString()
            waterGramsStr = if (rec.waterGrams % 1.0 == 0.0) rec.waterGrams.toInt().toString() else rec.waterGrams.toString()
            grindSize = rec.grindSize
            targetTempStr = rec.targetTempCelsius.toString()
            targetTimeSecStr = rec.targetTimeSeconds.toString()
            steps = rec.stepsText

            aiRecommendation = rec
            aiStatusMessage = "Divined parameters for $targetBean (${rec.numberOfPours} Pours • ${rec.waterGrams.toInt()}ml water • ${rec.targetTempCelsius}°C)"
            isAiGenerating = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "◆ INSCRIBE NEW SPELL ◆",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ==========================================
                // 16-BIT JRPG ORACLE DIALOGUE BOX
                // ==========================================
                PixelWindow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_ai_recipe_assistant"),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Portrait + Dialogue
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Oracle Portrait
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.primary)
                                    .background(Color.Black)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_pixel_barista_oracle),
                                    contentDescription = "Barista Oracle",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Dialogue Speech
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "◆ ARCH-MAGE ORACLE ◆",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "\"Speak thy beans and desired potion style! I shall calculate water, flame heat, grind size, and spiral pours!\"",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Bean Name Field
                        OutlinedTextField(
                            value = aiBeanName,
                            onValueChange = {
                                aiBeanName = it
                                if (coffeeOrigin.isBlank()) coffeeOrigin = it
                            },
                            label = { Text("Sacred Coffee Beans", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                            placeholder = { Text("e.g. Ethiopia Yirgacheffe, Colombia Geisha", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_ai_bean_name")
                        )

                        // Quick Inventory Beans Chips
                        if (beanBags.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                beanBags.forEach { bag ->
                                    val isChosen = aiBeanName.equals(bag.name, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .background(if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                                            .clickable {
                                                aiBeanName = bag.name
                                                if (coffeeOrigin.isBlank()) coffeeOrigin = bag.name
                                                roastLevel = bag.roastLevel
                                            }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                            .testTag("chip_inventory_bean_${bag.id}")
                                    ) {
                                        Text(
                                            text = "[ ${bag.name.uppercase()} ]",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = if (isChosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Intended Use Field
                        OutlinedTextField(
                            value = aiIntendedUse,
                            onValueChange = { aiIntendedUse = it },
                            label = { Text("Desired Potion Style / Goal", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                            placeholder = { Text("e.g. Fruity morning pour over, Rich espresso", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_ai_intended_use")
                        )

                        // Quick Goal Inspiration Chips
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "🌸 Fruity V60",
                                "☕ Rich Espresso",
                                "⚡ Smooth AeroPress",
                                "🧊 Flash Brew",
                                "🍫 French Press"
                            ).forEach { goal ->
                                val cleanGoal = goal.substringAfter(" ")
                                val isSelected = aiIntendedUse.contains(cleanGoal, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(1.dp, MaterialTheme.colorScheme.outline)
                                        .clickable { aiIntendedUse = cleanGoal }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                        .testTag("chip_goal_${cleanGoal.take(6)}")
                                ) {
                                    Text(
                                        text = goal,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Divination Button
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (isAiGenerating) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CONSULTING THE STARS...",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            } else {
                                PixelButton(
                                    text = "✨ CAST ORACLE DIVINATION",
                                    onClick = { executeAiRecommendation() },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "btn_recommend_with_ai"
                                )
                            }
                        }

                        // AI Recommendation Result Box
                        AnimatedVisibility(visible = aiRecommendation != null) {
                            val rec = aiRecommendation
                            if (rec != null) {
                                PixelWindow(
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = Color.Black.copy(alpha = 0.3f),
                                    borderColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "◆ DIVINATION COMPLETE ◆",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            text = "“${rec.baristaAdvice}”",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            PixelBadge(text = "WATER: ${rec.waterGrams.toInt()}ml (${rec.ratio})")
                                            PixelBadge(text = "TEMP: ${rec.targetTempCelsius}°C")
                                            PixelBadge(text = "GRIND: ${rec.grindSize}")
                                            PixelBadge(text = "POURS: ${rec.numberOfPours}")
                                            PixelBadge(text = "TIME: ${rec.targetTimeSeconds}s")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // RECIPE PARAMETERS FORM
                // ==========================================
                Text(
                    text = "◆ SPELL SPECIFICATIONS ◆",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Spell Name", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("e.g. Golden Bloom Pour", fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recipe_name")
                )

                // Brew Method Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Pour Over", "Espresso", "AeroPress", "Immersion").forEach { m ->
                        val isChosen = method.equals(m, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                            .clickable { method = m }
                            .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = m.split(" ").firstOrNull() ?: m,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isChosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Coffee & Water Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = coffeeGramsStr,
                        onValueChange = { coffeeGramsStr = it },
                        label = { Text("Dose (g)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_recipe_dose")
                    )

                    OutlinedTextField(
                        value = waterGramsStr,
                        onValueChange = { waterGramsStr = it },
                        label = { Text("Water (ml)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_recipe_water")
                    )
                }

                // Temp & Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = targetTempStr,
                        onValueChange = { targetTempStr = it },
                        label = { Text("Temp (°C)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_recipe_temp")
                    )

                    OutlinedTextField(
                        value = targetTimeSecStr,
                        onValueChange = { targetTimeSecStr = it },
                        label = { Text("Time (s)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_recipe_time")
                    )
                }

                // Grind Size
                OutlinedTextField(
                    value = grindSize,
                    onValueChange = { grindSize = it },
                    label = { Text("Grind Fineness", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recipe_grind")
                )

                // Instructions / Steps
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Brewing Incantation / Steps", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recipe_steps")
                )
            }
        },
        confirmButton = {
            PixelButton(
                text = "INSCRIBE SPELL",
                onClick = {
                    val coffeeG = coffeeGramsStr.toDoubleOrNull() ?: 15.0
                    val waterG = waterGramsStr.toDoubleOrNull() ?: 250.0
                    val targetTime = targetTimeSecStr.toIntOrNull() ?: 180
                    val targetTemp = targetTempStr.toIntOrNull() ?: 93
                    val ratioCalc = if (coffeeG > 0) "1:%.1f".format(Locale.US, waterG / coffeeG) else "1:16.7"

                    if (name.isNotBlank()) {
                        onSave(
                            name.trim(),
                            method,
                            coffeeOrigin.trim(),
                            roastLevel,
                            tastingNotes.trim(),
                            coffeeG,
                            waterG,
                            ratioCalc,
                            grindSize.trim().ifBlank { "Medium" },
                            targetTime,
                            targetTemp,
                            steps.trim()
                        )
                    }
                },
                testTag = "btn_confirm_add_recipe"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", fontFamily = FontFamily.Monospace)
            }
        }
    )
}
