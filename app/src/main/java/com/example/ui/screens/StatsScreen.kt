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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BeanBag
import com.example.ui.CoffeeHabits
import com.example.ui.CoffeeStats
import com.example.ui.theme.PixelBadge
import com.example.ui.theme.PixelButton
import com.example.ui.theme.PixelHpBar
import com.example.ui.theme.PixelWindow
import java.util.Locale

@Composable
fun StatsScreen(
    stats: CoffeeStats,
    beanBags: List<BeanBag>,
    onOpenAddBeanBag: () -> Unit,
    onOpenScanBag: () -> Unit = {},
    onOpenEditBeanWeight: (BeanBag) -> Unit,
    onToggleActiveBag: (BeanBag) -> Unit,
    onDeleteBeanBag: (Long) -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val activeBag = beanBags.firstOrNull { it.isActive }
    val currentLevel = (stats.totalBrews / 3) + 1

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Guild Status Header Window
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
                                    text = "◆ GUILD STATUS & INVENTORY ◆",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "ADVENTURER STATS & BEAN SACKS",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.primary)
                                    .clickable { onOpenSettings() }
                                    .testTag("btn_open_settings_stats"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "Theme Color Settings",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Guild Rank & Experience Bar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "⚔️ RANK: LVL $currentLevel BREWMASTER",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${stats.totalBrews * 125} EXP",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            PixelHpBar(
                                label = "PROGRESS TO NEXT RANK",
                                current = (stats.totalBrews * 125) % 375,
                                max = 375,
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 2. 2x2 Retro Stats Matrix
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PixelStatTile(
                            modifier = Modifier.weight(1f),
                            label = "POTIONS BREWED",
                            value = "${stats.totalBrews}",
                            detail = "${stats.weekBrews} THIS WEEK"
                        )
                        PixelStatTile(
                            modifier = Modifier.weight(1f),
                            label = "BEANS INFUSED",
                            value = "${stats.totalGrams.toInt()}g",
                            detail = "≈ %.1f SACKS".format(Locale.US, stats.totalGrams / 250.0)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PixelStatTile(
                            modifier = Modifier.weight(1f),
                            label = "AVG DOSE",
                            value = "%.1fg".format(Locale.US, stats.avgGramsPerCup),
                            detail = "PER CUP"
                        )
                        PixelStatTile(
                            modifier = Modifier.weight(1f),
                            label = "TOP METHOD",
                            value = stats.topRecipe.split(" ").firstOrNull()?.uppercase() ?: "—",
                            detail = "FAVORITE"
                        )
                    }
                }
            }

            // 3. Guild Achievements / Badges
            item {
                PixelWindow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    borderColor = MaterialTheme.colorScheme.outline
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "◆ GUILD FEATS & MEDALS ◆",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val feats = listOf(
                                Triple("☕ NOVICE", stats.totalBrews >= 1, "1st Brew"),
                                Triple("⚔️ ADEPT", stats.totalBrews >= 5, "5 Brews"),
                                Triple("👑 MASTER", stats.totalBrews >= 15, "15 Brews"),
                                Triple("🔮 SAGE", stats.totalBrews >= 30, "30 Brews")
                            )

                            feats.forEach { (title, unlocked, req) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (unlocked) MaterialTheme.colorScheme.primaryContainer else Color.Black.copy(alpha = 0.2f)
                                        )
                                        .border(
                                            1.dp,
                                            if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        )
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = title,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (unlocked) FontWeight.Black else FontWeight.Normal,
                                            fontSize = 9.sp,
                                            color = if (unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = if (unlocked) "DONE" else req,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp,
                                            color = if (unlocked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Equipped Bean Bag (Active Bag)
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
                            Text(
                                text = "◆ EQUIPPED BEAN POUCH ◆",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (activeBag != null) {
                                PixelBadge(text = "${activeBag.roastLevel.uppercase()} ROAST")
                            }
                        }

                        if (activeBag != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = activeBag.name.uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (activeBag.roaster.isNotBlank()) {
                                        Text(
                                            text = "ROASTER: ${activeBag.roaster.uppercase()}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    PixelButton(
                                        text = "⚖️ WEIGH",
                                        onClick = { onOpenEditBeanWeight(activeBag) }
                                    )
                                }
                            }

                            PixelHpBar(
                                label = "REMAINING SUPPLY: ${activeBag.remainingWeightGrams.toInt()}g / ${activeBag.initialWeightGrams.toInt()}g",
                                current = activeBag.remainingWeightGrams.toInt(),
                                max = activeBag.initialWeightGrams.toInt().coerceAtLeast(1),
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "No bean pouch equipped! Equip a bag below or scan a label to automatically deduct grams per brew.",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 5. Scan Bean Label Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .clickable { onOpenScanBag() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("btn_scan_bag_inventory_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📷", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "SCAN COFFEE BAG LABEL",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Auto-inscribe roaster, origin & weight",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        PixelButton(text = "SCAN", onClick = onOpenScanBag)
                    }
                }
            }

            // 6. All Bean Bags Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "◆ BEAN SACKS IN STOCK (${beanBags.size}) ◆",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    PixelButton(
                        text = "+ ADD SACK",
                        onClick = onOpenAddBeanBag,
                        testTag = "btn_add_bean_bag_header"
                    )
                }
            }

            // Bean Bags list
            items(beanBags, key = { it.id }) { bag ->
                PixelBeanBagCard(
                    bag = bag,
                    onToggleActive = { onToggleActiveBag(bag) },
                    onEditWeight = { onOpenEditBeanWeight(bag) },
                    onDelete = { onDeleteBeanBag(bag.id) }
                )
            }
        }

        // Floating Action Button to Add Bean Bag
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            PixelButton(
                text = "+ ADD BEAN SACK",
                onClick = onOpenAddBeanBag,
                testTag = "fab_add_bean_bag"
            )
        }
    }
}

@Composable
fun PixelStatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    detail: String
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.primary)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PixelBeanBagCard(
    bag: BeanBag,
    onToggleActive: () -> Unit,
    onEditWeight: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (bag.remainingWeightGrams / bag.initialWeightGrams).toFloat().coerceIn(0f, 1f)

    PixelWindow(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bean_bag_card_${bag.id}"),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = if (bag.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = bag.name.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (bag.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        if (bag.isActive) {
                            PixelBadge(text = "EQUIPPED")
                        }
                    }
                    if (bag.roaster.isNotBlank()) {
                        Text(
                            text = bag.roaster.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .background(if (bag.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.primary)
                            .clickable { onToggleActive() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (bag.isActive) "▶ ACTIVE" else "EQUIP",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (bag.isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

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

            PixelHpBar(
                label = "DURABILITY: ${bag.remainingWeightGrams.toInt()}g / ${bag.initialWeightGrams.toInt()}g",
                current = bag.remainingWeightGrams.toInt(),
                max = bag.initialWeightGrams.toInt().coerceAtLeast(1),
                barColor = if (bag.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}
