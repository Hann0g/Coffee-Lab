package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BeanBag
import com.example.data.CoffeeBrew
import com.example.data.CoffeeRecipe
import com.example.ui.CoffeeStats
import com.example.ui.LogSortOption
import com.example.ui.theme.PixelBadge
import com.example.ui.theme.PixelButton
import com.example.ui.theme.PixelHpBar
import com.example.ui.theme.PixelWindow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogScreen(
    brews: List<CoffeeBrew>,
    recipes: List<CoffeeRecipe>,
    activeBeanBag: BeanBag?,
    stats: CoffeeStats,
    filterRating: Int,
    sortOption: LogSortOption,
    onSetFilterRating: (Int) -> Unit,
    onSetSortOption: (LogSortOption) -> Unit,
    onOpenEditBeanWeight: (BeanBag) -> Unit,
    onOpenScanBag: () -> Unit = {},
    onOpenLogSheet: (CoffeeRecipe?) -> Unit,
    onOpenTimer: (CoffeeRecipe) -> Unit,
    onQuickLog: (CoffeeRecipe) -> Unit,
    onDeleteBrew: (Long) -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val todayDateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())
    var sortMenuExpanded by remember { mutableStateOf(false) }

    // Calculate Character Level & Title based on total brews
    val currentLevel = (stats.totalBrews / 3) + 1
    val currentExp = stats.totalBrews * 125
    val nextLevelExp = currentLevel * 375
    val characterTitle = when {
        currentLevel >= 15 -> "GRAND COFFEE ALCHEMIST"
        currentLevel >= 10 -> "HIGH BREW SAGE"
        currentLevel >= 5 -> "JOURNEYMAN ROASTER"
        else -> "NOVICE APPRENTICE"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top JRPG Guild Header & Character Status Card
            item {
                PixelWindow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title bar: Guild Name + Settings Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "◆ BARISTA'S SANCTUARY ◆",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        letterSpacing = 1.2.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "$characterTitle • LVL $currentLevel",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
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
                                    text = "+ QUEST",
                                    onClick = { onOpenLogSheet(null) },
                                    testTag = "btn_log_brew_header"
                                )
                            }
                        }

                        // 16:9 16-Bit Tavern Hero Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 8.5f)
                                .border(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_pixel_tavern_hero),
                                contentDescription = "Barista Guild Tavern",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Gradient Scrim overlay with retro stats
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                        )
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = todayDateStr.uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${stats.todayBrews} POTIONS CRAFTED TODAY",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.8.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Status Segmented Bars (HP & MP)
                        val caffeineHp = (stats.todayBrews * 35).coerceIn(15, 100)
                        val focusMp = (stats.todayBrews * 45).coerceIn(20, 100)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            PixelHpBar(
                                label = "☕ CAFFEINE POOL (HP)",
                                current = caffeineHp,
                                max = 100,
                                barColor = MaterialTheme.colorScheme.tertiary
                            )
                            PixelHpBar(
                                label = "⚡ DAILY FOCUS (MP)",
                                current = focusMp,
                                max = 100,
                                barColor = MaterialTheme.colorScheme.secondary
                            )
                            PixelHpBar(
                                label = "✨ COFFEE EXP",
                                current = currentExp,
                                max = nextLevelExp,
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 2. Active Bean Bag Item Pouch
            item {
                PixelWindow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    borderColor = MaterialTheme.colorScheme.outline
                ) {
                    if (activeBeanBag != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                        text = "🎒 ITEM POUCH:",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = activeBeanBag.name.uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    PixelButton(
                                        text = "⚖️ REWEIGH",
                                        onClick = { onOpenEditBeanWeight(activeBeanBag) },
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        borderColor = MaterialTheme.colorScheme.outline
                                    )
                                    PixelButton(
                                        text = "📷 SCAN",
                                        onClick = onOpenScanBag,
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        borderColor = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            PixelHpBar(
                                label = "SUPPLY DURABILITY",
                                current = activeBeanBag.remainingWeightGrams.toInt(),
                                max = activeBeanBag.initialWeightGrams.toInt().coerceAtLeast(1),
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenScanBag() }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🎒 POUCH EMPTY",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "Equip coffee beans to brew",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            PixelButton(
                                text = "📷 SCAN BAG",
                                onClick = onOpenScanBag
                            )
                        }
                    }
                }
            }

            // 3. Quick Cast Spells (Favorites Bar)
            if (recipes.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "◆ QUICK CAST SPELLS ◆",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 2.dp)
                        ) {
                            items(recipes.take(5)) { recipe ->
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, MaterialTheme.colorScheme.primary)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clickable { onQuickLog(recipe) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("quick_brew_${recipe.id}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "⚡ ${recipe.name.split(" ").firstOrNull() ?: recipe.name}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "[${recipe.defaultCoffeeGrams.toInt()}g]",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Guild Intel Glance
            if (stats.totalBrews > 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📜 GUILD INTEL:",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Favorite ritual time: ${stats.habits.usualTimeOfDay.uppercase()} • Avg %.1f cups/day".format(Locale.US, stats.habits.avgBrewsPerDay),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 5. Quest Chronicles Section Header + Filters
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◆ QUEST CHRONICLES (${brews.size}) ◆",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Sort Menu Dropdown Button
                        Box {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, MaterialTheme.colorScheme.outline)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { sortMenuExpanded = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("btn_sort_dropdown")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "▾ ${sortOption.displayName.uppercase()}",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                LogSortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.displayName.uppercase(),
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = if (sortOption == option) FontWeight.Black else FontWeight.Normal,
                                                fontSize = 11.sp
                                            )
                                        },
                                        onClick = {
                                            onSetSortOption(option)
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Rating Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            0 to "ALL",
                            5 to "★★★★★",
                            4 to "★★★★+",
                            3 to "★★★+"
                        ).forEach { (rating, label) ->
                            val isSelected = filterRating == rating
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                    .clickable { onSetFilterRating(rating) }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 6. Brew Cards List
            if (brews.isEmpty()) {
                item {
                    PixelWindow(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "☕",
                                fontSize = 36.sp
                            )
                            Text(
                                text = if (filterRating > 0) "NO MATCHING CHRONICLES" else "NO BREWS RECORDED YET",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (filterRating > 0) "Try clearing thy star filter above." else "Tap '+ QUEST' to chronicle thy first sacred elixir!",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(brews, key = { it.id }) { brew ->
                    PixelBrewCard(
                        brew = brew,
                        onDelete = { onDeleteBrew(brew.id) },
                        onTimer = {
                            val matchedRecipe = recipes.firstOrNull { it.name.equals(brew.recipeName, ignoreCase = true) }
                                ?: recipes.firstOrNull()
                            if (matchedRecipe != null) onOpenTimer(matchedRecipe)
                        }
                    )
                }
            }
        }

        // Floating Action Button: Pour Ritual Timer
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            PixelButton(
                text = "⏱️ RITUAL TIMER",
                onClick = {
                    val pourOverRecipe = recipes.firstOrNull { it.method.equals("Pour Over", ignoreCase = true) }
                        ?: recipes.firstOrNull()
                    if (pourOverRecipe != null) {
                        onOpenTimer(pourOverRecipe)
                    }
                },
                testTag = "fab_pour_timer"
            )
        }
    }
}

@Composable
fun PixelBrewCard(
    brew: CoffeeBrew,
    onDelete: () -> Unit,
    onTimer: () -> Unit
) {
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
    val dateText = dateFormatter.format(Date(brew.timestamp)).uppercase()
    val timeText = timeFormatter.format(Date(brew.timestamp)).uppercase()

    PixelWindow(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("brew_card_${brew.id}"),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Recipe Name + Ratio + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "◆ ${brew.recipeName.uppercase()} ◆",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        PixelBadge(text = brew.ratio)
                    }
                    Text(
                        text = "$dateText • $timeText",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
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

            // Retro Metrics Grid (DOSE | WATER | GRIND | RATING)
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
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "%.1fg".format(Locale.US, brew.coffeeGrams),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "WATER",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "${brew.waterGrams.toInt()}ml",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = "GRIND",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = brew.grindSize.split(" ").firstOrNull() ?: brew.grindSize,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "QUALITY",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val stars = "★".repeat(brew.rating.coerceIn(1, 5))
                        Text(
                            text = stars,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Notes / Bean Origin line
            if (brew.beanRoast.isNotBlank() || brew.notes.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (brew.beanRoast.isNotBlank()) {
                        Text(
                            text = "[ BEAN: ${brew.beanRoast.uppercase()} ]",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (brew.notes.isNotBlank()) {
                        val displayNotes = brew.notes.replace(Regex("\\[Pour Over: [^\\]]+\\]\n?"), "").trim()
                        if (displayNotes.isNotBlank()) {
                            Text(
                                text = "\"$displayNotes\"",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
