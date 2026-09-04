package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoffeeRecipe
import com.example.ui.model.PourPhase
import com.example.ui.model.PourScheduleCalculator
import java.util.Locale

enum class TimerMode {
    POUR_ASSISTANT,
    CLASSIC_STOPWATCH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewTimerSheet(
    sheetState: SheetState,
    recipe: CoffeeRecipe,
    isRunning: Boolean,
    elapsedSeconds: Int,
    onTogglePlayPause: () -> Unit,
    onReset: () -> Unit,
    onSeekSeconds: (Int) -> Unit = {},
    onFinishAndLog: (rating: Int, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var currentMode by remember { mutableStateOf(TimerMode.POUR_ASSISTANT) }
    var showAllStages by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    // Calculate smart pouring schedule based on recipe
    val pourPhases = remember(recipe) {
        PourScheduleCalculator.calculatePhases(
            method = recipe.method,
            coffeeGrams = recipe.defaultCoffeeGrams,
            waterGrams = recipe.defaultWaterGrams,
            targetTimeSeconds = recipe.targetTimeSeconds
        )
    }

    // Determine current active pour phase
    val currentPhaseIndex by remember(elapsedSeconds, pourPhases) {
        derivedStateOf {
            val idx = pourPhases.indexOfLast { elapsedSeconds >= it.startSeconds }
            if (idx == -1) 0 else idx.coerceAtMost(pourPhases.size - 1)
        }
    }

    val currentPhase = pourPhases.getOrNull(currentPhaseIndex) ?: pourPhases.first()
    val nextPhase = pourPhases.getOrNull(currentPhaseIndex + 1)

    // Haptic feedback trigger on phase transitions
    var lastVibratedPhaseIndex by remember { mutableIntStateOf(-1) }
    LaunchedEffect(currentPhaseIndex, isRunning) {
        if (isRunning && currentPhaseIndex != lastVibratedPhaseIndex) {
            lastVibratedPhaseIndex = currentPhaseIndex
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Overall formatted time
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = "%02d:%02d".format(Locale.US, minutes, seconds)

    val targetMin = recipe.targetTimeSeconds / 60
    val targetSec = recipe.targetTimeSeconds % 60
    val targetTimeFormatted = "%02d:%02d".format(Locale.US, targetMin, targetSec)

    // Current phase timing
    val phaseElapsed = (elapsedSeconds - currentPhase.startSeconds).coerceIn(0, currentPhase.durationSeconds)
    val phaseProgress = (phaseElapsed.toFloat() / currentPhase.durationSeconds.toFloat()).coerceIn(0f, 1f)
    val animatedPhaseProgress by animateFloatAsState(
        targetValue = phaseProgress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "phase_progress"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("brew_timer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Recipe & Method Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = recipe.method.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (recipe.coffeeOrigin.isNotBlank()) {
                            Text(
                                text = recipe.coffeeOrigin,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Mode Toggle Button (Pour Guide vs Classic)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            currentMode = if (currentMode == TimerMode.POUR_ASSISTANT) {
                                TimerMode.CLASSIC_STOPWATCH
                            } else {
                                TimerMode.POUR_ASSISTANT
                            }
                        }
                        .testTag("btn_toggle_timer_mode")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (currentMode == TimerMode.POUR_ASSISTANT) Icons.Default.Opacity else Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (currentMode == TimerMode.POUR_ASSISTANT) "Pour Mode" else "Stopwatch",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recipe quick metrics chip row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip("Dose", "${recipe.defaultCoffeeGrams.toInt()}g")
                MetricChip("Water", "${recipe.defaultWaterGrams.toInt()}g")
                MetricChip("Ratio", recipe.ratio)
                MetricChip("Grind", recipe.grindSize)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (currentMode == TimerMode.POUR_ASSISTANT) {
                // ==================== POUR TIMER ASSISTANT MODE ====================

                // Main Pour Assistant Stage Card
                val isDrawdownOrFinish = currentPhase.stageNumber == currentPhase.totalStages
                val cardBorderColor by animateColorAsState(
                    targetValue = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    label = "border_color"
                )

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(2.dp, cardBorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_active_pour_stage")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Stage header & pulse tag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "STAGE ${currentPhase.stageNumber} OF ${currentPhase.totalStages}: ${currentPhase.name.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "${formatSeconds(currentPhase.startSeconds)} - ${formatSeconds(currentPhase.endSeconds)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Digital Clock
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("text_timer_elapsed")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Target Scale Readout Box
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Scale,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isDrawdownOrFinish) "SCALE TARGET" else "POUR TO WATER WEIGHT",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${currentPhase.targetWaterGrams.toInt()} g",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                if (currentPhase.pourAmountGrams > 0.0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "+${currentPhase.pourAmountGrams.toInt()}g this pour",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Filter bed draining",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stage progress bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Stage Progress",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${formatSeconds(phaseElapsed)} / ${formatSeconds(currentPhase.durationSeconds)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { animatedPhaseProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                strokeCap = StrokeCap.Round
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Active Pouring Instruction & Pro Tip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = currentPhase.instruction,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (currentPhase.tip.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = currentPhase.tip,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Next Stage Preview
                        if (nextPhase != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Up Next: ${nextPhase.name} (${nextPhase.targetWaterGrams.toInt()}g) at ${formatSeconds(nextPhase.startSeconds)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                // ==================== CLASSIC MINIMALIST STOPWATCH MODE ====================
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isRunning) "BREWING IN PROGRESS" else if (elapsedSeconds > 0) "PAUSED" else "READY TO BREW",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target Total Time: $targetTimeFormatted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== TIMER CONTROLS ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Stage Button (if in pour mode)
                IconButton(
                    onClick = {
                        val prevIdx = (currentPhaseIndex - 1).coerceAtLeast(0)
                        val targetTime = pourPhases[prevIdx].startSeconds
                        onSeekSeconds(targetTime)
                    },
                    enabled = currentPhaseIndex > 0,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_timer_prev_stage")
                ) {
                    Icon(
                        Icons.Default.FastRewind,
                        contentDescription = "Previous Pour Stage",
                        tint = if (currentPhaseIndex > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }

                // Reset Button
                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_timer_reset")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset timer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Main Play / Pause Button
                Button(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(76.dp)
                        .testTag("btn_timer_play_pause"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play",
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Next Stage Button (jump straight to next pour)
                IconButton(
                    onClick = {
                        if (currentPhaseIndex < pourPhases.size - 1) {
                            val nextTime = pourPhases[currentPhaseIndex + 1].startSeconds
                            onSeekSeconds(nextTime)
                        } else {
                            onSeekSeconds(recipe.targetTimeSeconds)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_timer_next_stage")
                ) {
                    Icon(
                        Icons.Default.FastForward,
                        contentDescription = "Next Pour Stage",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== FULL POUR SCHEDULE TIMELINE ====================
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showAllStages = !showAllStages }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Opacity,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "POUR SCHEDULE (${pourPhases.size} PHASES)",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = if (showAllStages) "Hide ▲" else "View All ▼",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = showAllStages) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pourPhases.forEachIndexed { idx, phase ->
                                val isPast = elapsedSeconds >= phase.endSeconds
                                val isCurrent = idx == currentPhaseIndex
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    border = BorderStroke(
                                        width = if (isCurrent) 1.5.dp else 1.dp,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isCurrent) MaterialTheme.colorScheme.primary else if (isPast) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    if (isPast) {
                                                        Icon(
                                                            Icons.Default.Check,
                                                            contentDescription = "Done",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    } else {
                                                        Text(
                                                            text = "${phase.stageNumber}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Black,
                                                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            Column {
                                                Text(
                                                    text = phase.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = phase.instruction,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${phase.targetWaterGrams.toInt()}g",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = formatSeconds(phase.startSeconds),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recipe Custom Steps Guide (if present)
            if (recipe.steps.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "RECIPE NOTES & METHOD",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = recipe.steps,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== LOG DETAILS SECTION ====================
            // Cup Rating Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUP RATING",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(34.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            // Brew Notes TextField
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Tasting Notes / Pour Observations") },
                placeholder = { Text("e.g., Clean cup, balanced acidity, flat bed at 3:15") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_timer_notes"),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Finish & Log Button
            Button(
                onClick = { onFinishAndLog(rating, notes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_timer_finish"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Finish & Save to Brew Log",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatSeconds(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(Locale.US, m, s)
}
