package com.example.ui.theme

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 16-bit JRPG Window Frame.
 * Features the signature double border, corner pixel studs, and retro drop shadow.
 */
@Composable
fun PixelWindow(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    innerBorderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                val strokeOuter = 2.dp.toPx()
                val strokeInner = 1.dp.toPx()
                val cornerSize = 4.dp.toPx()

                // 1. Dark bottom-right retro shadow
                drawRect(
                    color = Color.Black.copy(alpha = 0.35f),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = size
                )

                // 2. Base window background
                drawRect(
                    color = backgroundColor,
                    topLeft = Offset.Zero,
                    size = size
                )

                // 3. Outer pixel border
                drawRect(
                    color = borderColor,
                    topLeft = Offset.Zero,
                    size = size,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeOuter)
                )

                // 4. Inner secondary pixel frame
                val inset = 3.dp.toPx()
                if (size.width > inset * 2 && size.height > inset * 2) {
                    drawRect(
                        color = innerBorderColor,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - inset * 2, size.height - inset * 2),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeInner)
                    )
                }

                // 5. Four signature corner pixel studs (■)
                val stud = cornerSize
                drawRect(borderColor, Offset(inset, inset), Size(stud, stud))
                drawRect(borderColor, Offset(size.width - inset - stud, inset), Size(stud, stud))
                drawRect(borderColor, Offset(inset, size.height - inset - stud), Size(stud, stud))
                drawRect(borderColor, Offset(size.width - inset - stud, size.height - inset - stud), Size(stud, stud))
            }
            .padding(8.dp)
    ) {
        content()
    }
}

/**
 * Chunky 16-bit JRPG HP / MP / EXP Segment Bar.
 */
@Composable
fun PixelHpBar(
    label: String,
    current: Int,
    max: Int,
    barColor: Color,
    modifier: Modifier = Modifier,
    emptyColor: Color = Color.Black.copy(alpha = 0.45f),
    totalSegments: Int = 10
) {
    val safeMax = if (max > 0) max else 1
    val ratio = (current.toFloat() / safeMax.toFloat()).coerceIn(0f, 1f)
    val filledSegments = (ratio * totalSegments).toInt().coerceIn(0, totalSegments)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = barColor
            )
            Text(
                text = "$current / $max",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Discrete Segment Blocks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.Black.copy(alpha = 0.3f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                .padding(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            for (i in 0 until totalSegments) {
                val isFilled = i < filledSegments
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .background(if (isFilled) barColor else emptyColor)
                )
            }
        }
    }
}

/**
 * Chunky 16-bit Beveled Button with 3D shadow and click displacement.
 */
@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    testTag: String = "pixel_btn",
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val translationY = if (isPressed) 2.dp else 0.dp
    val shadowHeight = if (isPressed) 1.dp else 3.dp

    Box(
        modifier = modifier
            .testTag(testTag)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .graphicsLayer {
                this.translationY = translationY.toPx()
            }
            .drawBehind {
                // Bottom 3D shadow block
                drawRect(
                    color = Color.Black.copy(alpha = 0.4f),
                    topLeft = Offset(0f, size.height),
                    size = Size(size.width, shadowHeight.toPx())
                )
            }
            .background(if (enabled) containerColor else containerColor.copy(alpha = 0.5f))
            .border(2.dp, borderColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = if (enabled) contentColor else contentColor.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Retro bracketed JRPG badge e.g. [ LVL. 12 ] or [ 1:16.7 ]
 */
@Composable
fun PixelBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = color.copy(alpha = 0.15f)
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.6f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "[ $text ]",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            color = color
        )
    }
}

/**
 * Classic JRPG Dialogue Box with blinking cursor.
 */
@Composable
fun PixelDialogueBox(
    speakerName: String,
    text: String,
    modifier: Modifier = Modifier,
    speakerTitle: String = "SAGE OF COFFEE",
    avatarContent: (@Composable () -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor"
    )

    PixelWindow(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Speaker Plate
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "◆ $speakerName ◆",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = speakerTitle,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Dialogue content with optional avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (avatarContent != null) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary)
                            .background(Color.Black.copy(alpha = 0.2f))
                    ) {
                        avatarContent()
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = text,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Blinking arrow cursor (classic retro JRPG prompt)
                    Text(
                        text = "▶",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}
