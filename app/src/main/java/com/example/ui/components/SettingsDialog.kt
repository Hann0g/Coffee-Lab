package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AppThemeColor
import com.example.ui.theme.PixelBadge
import com.example.ui.theme.PixelButton
import com.example.ui.theme.PixelHpBar
import com.example.ui.theme.PixelWindow

@Composable
fun SettingsDialog(
    selectedThemeColor: AppThemeColor,
    trackedCoffees: Int,
    onSelectThemeColor: (AppThemeColor) -> Boolean,
    onDismiss: () -> Unit
) {
    var lockedThemeNotice by remember { mutableStateOf<String?>(null) }
    val nextLockedTheme = AppThemeColor.entries.firstOrNull { !it.isUnlocked(trackedCoffees) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PixelWindow(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("dialog_settings"),
            backgroundColor = MaterialTheme.colorScheme.surface,
            borderColor = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "◆ INNKEEPER'S PALETTES ◆",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "REALM COLOR & THEME CUSTOMIZATION",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Progression HP Bar
                PixelWindow(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Black.copy(alpha = 0.25f),
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⚔️ POTIONS LOGGED: $trackedCoffees",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (nextLockedTheme != null) {
                                Text(
                                    text = "NEXT: ${nextLockedTheme.displayName.uppercase()}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (nextLockedTheme != null) {
                            PixelHpBar(
                                label = "EXP TO UNLOCK ${nextLockedTheme.displayName.uppercase()}",
                                current = trackedCoffees,
                                max = nextLockedTheme.requiredCoffees,
                                barColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "👑 MASTER ALCHEMIST: ALL PALETTES UNLOCKED!",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Locked notice banner
                AnimatedVisibility(visible = lockedThemeNotice != null) {
                    if (lockedThemeNotice != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
                                .border(1.dp, MaterialTheme.colorScheme.error)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lockedThemeNotice ?: "",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "✕",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.clickable { lockedThemeNotice = null }
                                )
                            }
                        }
                    }
                }

                // Color Palettes
                Text(
                    text = "◆ SELECT COLOR CODEX (5 CHOICES) ◆",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppThemeColor.entries.forEach { option ->
                        val isSelected = option == selectedThemeColor
                        val isUnlocked = option.isUnlocked(trackedCoffees)
                        val remaining = option.requiredCoffees - trackedCoffees

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                                .clickable {
                                    if (isUnlocked) {
                                        lockedThemeNotice = null
                                        onSelectThemeColor(option)
                                    } else {
                                        lockedThemeNotice = "🔒 Locked! Brew $remaining more potions to unlock ${option.displayName}."
                                    }
                                }
                                .padding(8.dp)
                                .testTag("theme_color_${option.id}")
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
                                    // Color swatch box
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(option.previewColor)
                                            .border(1.dp, Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!isUnlocked) {
                                            Text(text = "🔒", fontSize = 10.sp)
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = option.displayName.uppercase(),
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = if (isUnlocked) option.description.uppercase() else "REQ: ${option.requiredCoffees} POTIONS ($trackedCoffees/${option.requiredCoffees})",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    PixelBadge(text = "EQUIPPED")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Done Button
                PixelButton(
                    text = "✦ DEPART SANCTUARY ✦",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_close_settings"
                )
            }
        }
    }
}
