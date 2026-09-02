package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun AddRecipeDialog(
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
    var name by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("Pour Over") }
    var coffeeOrigin by remember { mutableStateOf("") }
    var roastLevel by remember { mutableStateOf("Medium") }
    var tastingNotes by remember { mutableStateOf("") }
    var coffeeGramsStr by remember { mutableStateOf("18") }
    var waterGramsStr by remember { mutableStateOf("300") }
    var grindSize by remember { mutableStateOf("Medium-Fine") }
    var targetTimeSecStr by remember { mutableStateOf("180") }
    var targetTempStr by remember { mutableStateOf("93") }
    var steps by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Custom Recipe",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recipe Name") },
                    placeholder = { Text("e.g. Morning V60 Bloom") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recipe_name"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Method selection chips
                Text(
                    text = "BREW METHOD",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Pour Over", "Espresso", "AeroPress", "Immersion").forEach { m ->
                        val isSelected = method == m
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { method = m }
                        ) {
                            Text(
                                text = m,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Origin & Tasting Notes
                OutlinedTextField(
                    value = coffeeOrigin,
                    onValueChange = { coffeeOrigin = it },
                    label = { Text("Coffee Origin / Variety") },
                    placeholder = { Text("e.g. Ethiopia Yirgacheffe, Colombia Huila") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recipe_origin"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Roast Level selector
                Text(
                    text = "ROAST LEVEL",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Light", "Medium", "Dark").forEach { r ->
                        val isSelected = roastLevel == r
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { roastLevel = r }
                        ) {
                            Text(
                                text = r,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = tastingNotes,
                    onValueChange = { tastingNotes = it },
                    label = { Text("Tasting Notes") },
                    placeholder = { Text("e.g. Floral, Bergamot, Blueberry, Caramel") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recipe_tasting_notes"),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = coffeeGramsStr,
                        onValueChange = { coffeeGramsStr = it },
                        label = { Text("Coffee (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = waterGramsStr,
                        onValueChange = { waterGramsStr = it },
                        label = { Text("Water (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = targetTimeSecStr,
                        onValueChange = { targetTimeSecStr = it },
                        label = { Text("Target Time (s)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = targetTempStr,
                        onValueChange = { targetTempStr = it },
                        label = { Text("Temp (°C)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = grindSize,
                    onValueChange = { grindSize = it },
                    label = { Text("Grind Size") },
                    placeholder = { Text("Medium-Fine, Fine, Coarse") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Steps / Method Details") },
                    placeholder = { Text("1. Bloom with 45g for 40s\n2. Spiral pour to 300g") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val coffeeG = coffeeGramsStr.toDoubleOrNull() ?: 18.0
                    val waterG = waterGramsStr.toDoubleOrNull() ?: 300.0
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("btn_confirm_add_recipe")
            ) {
                Text("Save Recipe", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )
}


