package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BeanBag
import com.example.data.CoffeeRecipe
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBrewSheet(
    sheetState: SheetState,
    recipes: List<CoffeeRecipe>,
    beanBags: List<BeanBag>,
    initialRecipe: CoffeeRecipe? = null,
    onDismiss: () -> Unit,
    onSave: (
        recipeName: String,
        coffeeGrams: Double,
        waterGrams: Double,
        ratio: String,
        grindSize: String,
        brewTimeSeconds: Int,
        waterTempCelsius: Int,
        beanRoast: String,
        rating: Int,
        notes: String,
        bagId: Long?
    ) -> Unit
) {
    val defaultRec = initialRecipe ?: recipes.firstOrNull()
    var selectedRecipe by remember { mutableStateOf(defaultRec) }

    val isDefaultPourOver = defaultRec?.method.equals("Pour Over", ignoreCase = true) ||
            defaultRec?.name?.contains("V60", ignoreCase = true) == true ||
            defaultRec?.name?.contains("Chemex", ignoreCase = true) == true ||
            defaultRec?.name?.contains("Pour Over", ignoreCase = true) == true ||
            defaultRec == null

    var selectedMethod by remember {
        mutableStateOf(if (isDefaultPourOver) "Pour Over" else (defaultRec?.method ?: "Pour Over"))
    }
    val isPourOver = selectedMethod.equals("Pour Over", ignoreCase = true)

    // Pour Over Specific Options State
    var selectedDripper by remember {
        mutableStateOf(
            if (defaultRec?.name?.contains("Chemex", ignoreCase = true) == true) "Chemex"
            else "Hario V60"
        )
    }
    var selectedTechnique by remember { mutableStateOf("4:6 Method") }
    var bloomWaterGrams by remember { mutableDoubleStateOf(45.0) }
    var bloomTimeSeconds by remember { mutableIntStateOf(45) }
    var selectedFilterPaper by remember { mutableStateOf("White Tabbed") }
    var selectedAgitation by remember { mutableStateOf("Gentle Swirl") }
    var showPourOverDetails by remember { mutableStateOf(true) }

    var coffeeGrams by remember { mutableDoubleStateOf(defaultRec?.defaultCoffeeGrams ?: 18.0) }
    var waterGrams by remember { mutableDoubleStateOf(defaultRec?.defaultWaterGrams ?: 300.0) }
    var grindSize by remember { mutableStateOf(defaultRec?.grindSize ?: "Medium") }
    var waterTemp by remember { mutableIntStateOf(defaultRec?.targetTempCelsius ?: 93) }
    var rating by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }

    val activeBag = beanBags.firstOrNull { it.isActive }
    var selectedBagId by remember { mutableStateOf(activeBag?.id) }

    val ratioMultiplier = if (coffeeGrams > 0) waterGrams / coffeeGrams else 16.0
    val ratioText = "1:%.1f".format(Locale.US, ratioMultiplier)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("log_brew_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log Brew",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = ratioText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Method Selector Chips
            Text(
                text = "BREW METHOD",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val brewMethods = listOf("Pour Over", "Espresso", "AeroPress", "French Press", "Cold Brew")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(brewMethods) { method ->
                    val isSelected = selectedMethod.equals(method, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedMethod = method
                                val matchingRecipe = recipes.firstOrNull { it.method.equals(method, ignoreCase = true) }
                                if (matchingRecipe != null) {
                                    selectedRecipe = matchingRecipe
                                    coffeeGrams = matchingRecipe.defaultCoffeeGrams
                                    waterGrams = matchingRecipe.defaultWaterGrams
                                    grindSize = matchingRecipe.grindSize
                                    waterTemp = matchingRecipe.targetTempCelsius
                                }
                            }
                            .testTag("method_chip_${method.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (method == "Pour Over") Icons.Default.WaterDrop else Icons.Default.Coffee,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = method,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recipe selection chips
            val filteredRecipes = recipes.filter { it.method.equals(selectedMethod, ignoreCase = true) }.ifEmpty { recipes.take(4) }
            Text(
                text = "RECIPE PRESET",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredRecipes.take(4).forEach { recipe ->
                    val isSelected = selectedRecipe?.id == recipe.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedRecipe = recipe
                                coffeeGrams = recipe.defaultCoffeeGrams
                                waterGrams = recipe.defaultWaterGrams
                                grindSize = recipe.grindSize
                                waterTemp = recipe.targetTempCelsius
                                if (recipe.name.contains("Chemex", ignoreCase = true)) {
                                    selectedDripper = "Chemex"
                                } else if (recipe.name.contains("V60", ignoreCase = true)) {
                                    selectedDripper = "Hario V60"
                                }
                            }
                    ) {
                        Text(
                            text = recipe.name.split(" ").firstOrNull() ?: recipe.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            // POUR OVER OPTIONS CARD (Shown specifically when method is Pour Over)
            if (isPourOver) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_pour_over_options")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(14.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "POUR OVER OPTIONS",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "$selectedDripper · $selectedTechnique",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showPourOverDetails = !showPourOverDetails }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (showPourOverDetails) "Collapse" else "Options",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Icon(
                                        imageVector = if (showPourOverDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (showPourOverDetails) {
                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Dripper / Brewer selection
                            Text(
                                text = "DRIPPER / BREWER",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val drippers = listOf(
                                    "Hario V60" to "Conical 60°",
                                    "Kalita Wave" to "Flat-Bottom 3-Hole",
                                    "Chemex" to "Thick Paper Filter",
                                    "Origami" to "20-Rib Hybrid",
                                    "Fellow Stagg" to "Insulated Flat",
                                    "Clever Dripper" to "Immersion Hybrid"
                                )
                                items(drippers) { (dripName, dripSub) ->
                                    val isChosen = selectedDripper == dripName
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedDripper = dripName
                                                if (dripName == "Chemex") {
                                                    grindSize = "Medium-Coarse"
                                                } else if (dripName == "Kalita Wave") {
                                                    grindSize = "Medium"
                                                } else if (dripName == "Hario V60" && selectedTechnique != "4:6 Method") {
                                                    grindSize = "Medium-Fine"
                                                }
                                            }
                                            .testTag("btn_dripper_${dripName.lowercase().replace(" ", "_")}")
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            Text(
                                                text = dripName,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = dripSub,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 10.sp,
                                                color = if (isChosen) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. Pour Technique
                            Text(
                                text = "POURING TECHNIQUE",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val techniques = listOf(
                                    Triple("4:6 Method", "Tetsu Kasuya", "5-Pour balance (Sweet/Strength)"),
                                    Triple("Hoffmann 2-Pour", "James Hoffmann", "60% + 40% high clarity"),
                                    Triple("3-Stage Pulse", "Classic Balanced", "Even saturation & full body"),
                                    Triple("Continuous Spiral", "Gentle Spiral", "Smooth constant flow"),
                                    Triple("Osmotic Flow", "Kafedokoro", "Low-agitation center trickle")
                                )
                                items(techniques) { (techName, author, desc) ->
                                    val isChosen = selectedTechnique == techName
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isChosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                        ),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedTechnique = techName
                                                if (techName == "4:6 Method") {
                                                    grindSize = "Medium-Coarse"
                                                } else if (techName == "Hoffmann 2-Pour") {
                                                    grindSize = "Medium-Fine"
                                                } else if (techName == "Osmotic Flow") {
                                                    grindSize = "Coarse"
                                                }
                                            }
                                            .testTag("btn_technique_${techName.lowercase().replace(" ", "_")}")
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            Text(
                                                text = techName,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChosen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = author,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3. Bloom Stage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "BLOOM STAGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${bloomWaterGrams.toInt()}g water · ${bloomTimeSeconds}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "2x" to (coffeeGrams * 2.0),
                                    "3x" to (coffeeGrams * 3.0),
                                    "45g" to 45.0,
                                    "60g" to 60.0
                                ).forEach { (label, grams) ->
                                    val isChosen = bloomWaterGrams.toInt() == grams.toInt()
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isChosen) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChosen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { bloomWaterGrams = grams }
                                    ) {
                                        Text(
                                            text = "$label (${grams.toInt()}g)",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isChosen) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            // Bloom duration chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(30 to "30s Fast", 45 to "45s Standard", 60 to "60s Extended").forEach { (sec, label) ->
                                    val isChosen = bloomTimeSeconds == sec
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isChosen) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChosen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { bloomTimeSeconds = sec }
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isChosen) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4. Filter Paper & Agitation Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Filter Paper
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "FILTER PAPER",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    listOf("White Tabbed", "Natural Brown", "Sibarist Fast").forEach { filter ->
                                        val isChosen = selectedFilterPaper == filter
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isChosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { selectedFilterPaper = filter }
                                        ) {
                                            Text(
                                                text = filter,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isChosen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Agitation
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "AGITATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    listOf("Gentle Swirl", "Spoon Excavate", "None (Stream)").forEach { agit ->
                                        val isChosen = selectedAgitation == agit
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isChosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { selectedAgitation = agit }
                                        ) {
                                            Text(
                                                text = agit,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isChosen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 5. Dynamic Live Pour Roadmap
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.WaterDrop,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "POUR ROADMAP (${waterGrams.toInt()}g total)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val roadmapText = when (selectedTechnique) {
                                        "4:6 Method" -> {
                                            val pourSize = (waterGrams / 5.0).toInt()
                                            "Bloom: ${pourSize}g (0:00) ➔ 2nd: ${pourSize * 2}g (0:45) ➔ 3rd: ${pourSize * 3}g (1:30) ➔ 4th: ${pourSize * 4}g (2:15) ➔ 5th: ${waterGrams.toInt()}g (3:00)"
                                        }
                                        "Hoffmann 2-Pour" -> {
                                            val bloom = bloomWaterGrams.toInt()
                                            val pour1 = (waterGrams * 0.6).toInt()
                                            "Bloom: ${bloom}g (0:00) ➔ Pour 1: ${pour1}g (0:45) ➔ Pour 2: ${waterGrams.toInt()}g (1:15) ➔ Drawdown ~3:30"
                                        }
                                        "3-Stage Pulse" -> {
                                            val bloom = bloomWaterGrams.toInt()
                                            val remaining = waterGrams - bloom
                                            val p1 = bloom + (remaining * 0.5).toInt()
                                            "Bloom: ${bloom}g (0:00) ➔ Pulse 1: ${p1}g (0:45) ➔ Pulse 2: ${waterGrams.toInt()}g (1:30) ➔ Drawdown ~3:15"
                                        }
                                        "Continuous Spiral" -> {
                                            "Bloom: ${bloomWaterGrams.toInt()}g (0:00) ➔ Steady Spiral to ${waterGrams.toInt()}g (0:45) ➔ Drawdown ~2:45"
                                        }
                                        else -> {
                                            "Bloom: ${bloomWaterGrams.toInt()}g (0:00) ➔ Center Trickle to ${waterGrams.toInt()}g (0:40) ➔ Drawdown ~3:45"
                                        }
                                    }
                                    Text(
                                        text = roadmapText,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dose Stepper (Grams of Coffee)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "COFFEE DOSE",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                if (coffeeGrams > 5.0) {
                                    coffeeGrams = (coffeeGrams - 0.5).coerceAtLeast(1.0)
                                    waterGrams = (coffeeGrams * ratioMultiplier).toInt().toDouble()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("btn_dose_minus")
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrease dose",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = "%.1fg".format(Locale.US, coffeeGrams),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        IconButton(
                            onClick = {
                                coffeeGrams = (coffeeGrams + 0.5).coerceAtMost(100.0)
                                waterGrams = (coffeeGrams * ratioMultiplier).toInt().toDouble()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("btn_dose_plus")
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Increase dose",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset quick grams chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        listOf(12.0, 15.0, 18.0, 20.0, 30.0).forEach { presetGrams ->
                            val isChosen = coffeeGrams == presetGrams
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        coffeeGrams = presetGrams
                                        waterGrams = (coffeeGrams * ratioMultiplier).toInt().toDouble()
                                    }
                            ) {
                                Text(
                                    text = "${presetGrams.toInt()}g",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isChosen) FontWeight.Black else FontWeight.Medium,
                                    color = if (isChosen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Water Stepper
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WATER YIELD",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${waterGrams.toInt()} ml / g",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                if (waterGrams > 20) waterGrams -= 10
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus water")
                        }

                        IconButton(
                            onClick = { waterGrams += 10 },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add water")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grind Size selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GRIND SIZE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = grindSize,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Fine", "Med-Fine", "Medium", "Med-Coarse", "Coarse").forEach { size ->
                    val isSelected = grindSize.equals(size, ignoreCase = true) ||
                            (size == "Med-Fine" && grindSize == "Medium-Fine") ||
                            (size == "Med-Coarse" && grindSize == "Medium-Coarse")
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                grindSize = if (size == "Med-Fine") "Medium-Fine"
                                else if (size == "Med-Coarse") "Medium-Coarse"
                                else size
                            }
                    ) {
                        Text(
                            text = size,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Water Temperature selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WATER TEMPERATURE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${waterTemp}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(88, 90, 92, 94, 96).forEach { temp ->
                    val isSelected = waterTemp == temp
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { waterTemp = temp }
                            .testTag("temp_chip_$temp")
                    ) {
                        Text(
                            text = "${temp}°C",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bean selection
            if (beanBags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COFFEE BEANS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    beanBags.take(3).forEach { bag ->
                        val isSelected = selectedBagId == bag.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedBagId = bag.id }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = bag.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${bag.remainingWeightGrams.toInt()}g left",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Rating Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RATING",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Star $star",
                                tint = if (star <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notes input
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Tasting Notes / Details (optional)") },
                placeholder = { Text("e.g. Sweet, floral notes, clean finish") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_notes"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val finalRecipeName = if (isPourOver) {
                        if (selectedRecipe != null && !selectedRecipe!!.name.startsWith("V60") && !selectedRecipe!!.name.startsWith("Chemex") && !selectedRecipe!!.name.equals("Custom Brew")) {
                            "${selectedRecipe!!.name} ($selectedDripper)"
                        } else {
                            "$selectedDripper ($selectedTechnique)"
                        }
                    } else {
                        selectedRecipe?.name ?: "Custom Brew"
                    }

                    val roastName = beanBags.find { it.id == selectedBagId }?.name ?: "House Blend"
                    val targetTime = if (isPourOver) {
                        when (selectedTechnique) {
                            "4:6 Method" -> 210
                            "Hoffmann 2-Pour" -> 210
                            "3-Stage Pulse" -> 195
                            "Single Continuous" -> 165
                            "Osmotic Flow" -> 240
                            else -> 180
                        }
                    } else {
                        selectedRecipe?.targetTimeSeconds ?: 180
                    }

                    val pourOverMetadata = if (isPourOver) {
                        "[Pour Over: $selectedDripper | $selectedTechnique | Bloom: ${bloomWaterGrams.toInt()}g (${bloomTimeSeconds}s) | Filter: $selectedFilterPaper | Agitation: $selectedAgitation]"
                    } else ""

                    val finalNotes = if (pourOverMetadata.isNotBlank()) {
                        if (notes.isNotBlank()) "$notes\n$pourOverMetadata" else pourOverMetadata
                    } else {
                        notes
                    }

                    onSave(
                        finalRecipeName,
                        coffeeGrams,
                        waterGrams,
                        ratioText,
                        grindSize,
                        targetTime,
                        waterTemp,
                        roastName,
                        rating,
                        finalNotes,
                        selectedBagId
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_save_brew"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Coffee, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Brew (%.1fg)".format(Locale.US, coffeeGrams),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

