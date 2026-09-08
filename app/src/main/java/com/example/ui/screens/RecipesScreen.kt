package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoffeeRecipe
import com.example.ui.RecipeRatingSummary
import com.example.ui.theme.PixelBadge
import com.example.ui.theme.PixelButton
import com.example.ui.theme.PixelWindow
import java.util.Locale

@Composable
fun RecipesScreen(
    recipes: List<CoffeeRecipe>,
    recipeRatings: Map<String, RecipeRatingSummary> = emptyMap(),
    onOpenAddRecipeDialog: () -> Unit,
    onStartTimer: (CoffeeRecipe) -> Unit,
    onOpenLogSheet: (CoffeeRecipe) -> Unit,
    onDeleteRecipe: (Long) -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    var showRatioCalculator by remember { mutableStateOf(false) }
    var calcCoffeeGrams by remember { mutableDoubleStateOf(18.0) }
    var calcRatio by remember { mutableDoubleStateOf(16.7) }
    var showCoffeeDoseDialog by remember { mutableStateOf(false) }
    var doseInputText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header Window
            item {
                PixelWindow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "◆ SACRED GRIMOIRE ◆",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "ANCIENT INFUSION FORMULAS",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .border(1.dp, MaterialTheme.colorScheme.primary)
                                        .clickable { onOpenSettings() }
                                        .testTag("btn_open_settings"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Palette,
                                        contentDescription = "Theme Color Settings",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                PixelButton(
                                    text = "+ INSCRIBE",
                                    onClick = onOpenAddRecipeDialog,
                                    testTag = "btn_add_recipe_header"
                                )
                            }
                        }

                        // Toggle Ratio Alchemy Tablet
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                .clickable { showRatioCalculator = !showRatioCalculator }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("btn_toggle_ratio_calc")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "🔮 ALCHEMICAL RATIO SCROLL",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = if (showRatioCalculator) "▲ COLLAPSE" else "▼ REVEAL",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Ratio Alchemy Calculator Box
            if (showRatioCalculator) {
                item {
                    val calcWater = calcCoffeeGrams * calcRatio
                    PixelWindow(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        borderColor = MaterialTheme.colorScheme.primary
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "◆ WATER & COFFEE PROPORTIONS ◆",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.25f))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.clickable {
                                        doseInputText = if (calcCoffeeGrams % 1.0 == 0.0) calcCoffeeGrams.toInt().toString() else calcCoffeeGrams.toString()
                                        showCoffeeDoseDialog = true
                                    }
                                ) {
                                    Text(
                                        text = "COFFEE BEANS",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "%.1fg ✎".format(Locale.US, calcCoffeeGrams),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "➔",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "WATER FLUID",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "${calcWater.toInt()}ml",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Slider
                            Text(
                                text = "Dose: ${calcCoffeeGrams.toInt()}g",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = calcCoffeeGrams.toFloat().coerceIn(10f, 60f),
                                onValueChange = { calcCoffeeGrams = it.toDouble() },
                                valueRange = 10f..60f,
                                steps = 50,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            // Quick preset ratios
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(15.0 to "1:15 (STRONG)", 16.7 to "1:16.7 (V60)", 18.0 to "1:18 (LIGHT)").forEach { (ratio, label) ->
                                    val isCurrent = (calcRatio - ratio).let { Math.abs(it) < 0.2 }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                            .border(1.dp, if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                                            .clickable { calcRatio = ratio }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recipe Items (Grimoire Spells)
            items(recipes, key = { it.id }) { recipe ->
                val ratingSummary = recipeRatings[recipe.name]
                PixelRecipeCard(
                    recipe = recipe,
                    ratingSummary = ratingSummary,
                    onStartTimer = { onStartTimer(recipe) },
                    onOpenLogSheet = { onOpenLogSheet(recipe) },
                    onDelete = { onDeleteRecipe(recipe.id) }
                )
            }
        }

        // Floating Action Button to Inscribe Recipe
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            PixelButton(
                text = "+ INSCRIBE SPELL",
                onClick = onOpenAddRecipeDialog,
                testTag = "fab_add_recipe"
            )
        }

        // Dialog to manually enter coffee dose
        if (showCoffeeDoseDialog) {
            AlertDialog(
                onDismissRequest = { showCoffeeDoseDialog = false },
                title = {
                    Text(
                        text = "◆ ENTER COFFEE DOSE ◆",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Inscribe thy desired coffee dose (grams):",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        OutlinedTextField(
                            value = doseInputText,
                            onValueChange = { doseInputText = it },
                            label = { Text("Dose (grams)", fontFamily = FontFamily.Monospace) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_coffee_dose_field")
                        )
                    }
                },
                confirmButton = {
                    PixelButton(
                        text = "SAVE",
                        onClick = {
                            val parsed = doseInputText.trim().replace(',', '.').toDoubleOrNull()
                            if (parsed != null && parsed > 0) {
                                calcCoffeeGrams = parsed.coerceIn(1.0, 500.0)
                            }
                            showCoffeeDoseDialog = false
                        },
                        testTag = "btn_save_dose"
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showCoffeeDoseDialog = false }) {
                        Text("CANCEL", fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    }
}

@Composable
fun PixelRecipeCard(
    recipe: CoffeeRecipe,
    ratingSummary: RecipeRatingSummary? = null,
    onStartTimer: () -> Unit,
    onOpenLogSheet: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val min = recipe.targetTimeSeconds / 60
    val sec = recipe.targetTimeSeconds % 60
    val timeFormatted = if (min > 60) "${min / 60}h" else "%d:%02d".format(Locale.US, min, sec)

    PixelWindow(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recipe_card_${recipe.id}"),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recipe Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "◆ ${recipe.name.uppercase()} ◆",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "[ SPELL: ${recipe.method.uppercase()} ]",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        if (ratingSummary != null && ratingSummary.count > 0) {
                            Text(
                                text = "★ %.1f (%d)".format(Locale.US, ratingSummary.averageRating, ratingSummary.count),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                PixelBadge(text = recipe.ratio)
            }

            // Specs Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DOSE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "%.0fg".format(Locale.US, recipe.defaultCoffeeGrams),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "WATER",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "%.0fml".format(Locale.US, recipe.defaultWaterGrams),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "GRIND",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = recipe.grindSize.split(" ").firstOrNull() ?: recipe.grindSize,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "TIME",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = timeFormatted,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Steps preview accordion
            if (recipe.steps.isNotBlank()) {
                AnimatedVisibility(visible = isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "◆ BREWING INCANTATION ◆",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = recipe.steps,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (recipe.steps.isNotBlank()) {
                    Text(
                        text = if (isExpanded) "▲ HIDE STEPS" else "▼ READ STEPS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PixelButton(
                        text = "☕ LOG",
                        onClick = onOpenLogSheet,
                        testTag = "btn_log_recipe_${recipe.id}"
                    )

                    PixelButton(
                        text = "⏱️ TIMER",
                        onClick = onStartTimer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        borderColor = MaterialTheme.colorScheme.secondary,
                        testTag = "btn_brew_timer_${recipe.id}"
                    )

                    if (!recipe.isPreset) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
