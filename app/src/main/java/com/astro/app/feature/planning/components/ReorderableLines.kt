package com.astro.app.feature.planning.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.planning.PlanningEntry
import com.astro.app.feature.planning.DragOrder

/**
 * Lignes du planning que l'on réordonne comme des briques.
 *
 * Un appui long sur la poignée d'une ligne la **soulève** — elle grossit légèrement et
 * prend une ombre, un retour haptique confirme la prise — puis elle suit le doigt. Les
 * autres lignes **glissent** pour lui céder la place dès qu'elle a franchi leur moitié.
 * Au lâcher, l'ordre est enregistré.
 *
 * La ligne vide finale ne se saisit pas et reste toujours la dernière.
 *
 * Seule la poignée réagit à l'appui long : sur le champ de texte, ce geste appartient
 * à Android, qui s'en sert pour sélectionner du texte.
 */
@Composable
fun ReorderableLines(
    lines: List<PlanningEntry>,
    onMove: (id: Long, toIndex: Int) -> Unit,
    row: @Composable (line: PlanningEntry, handle: Modifier?) -> Unit,
) {
    val spacing = AstroTheme.dimens.spaceSm
    val spacingPx = with(LocalDensity.current) { spacing.toPx() }
    val haptics = LocalHapticFeedback.current
    val heights = remember { mutableStateMapOf<Long, Int>() }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    var offset by remember { mutableFloatStateOf(0f) }
    val currentLines by rememberUpdatedState(lines)
    val currentOnMove by rememberUpdatedState(onMove)
    val palette = AstroTheme.palette
    val shape = RoundedCornerShape(AstroTheme.dimens.radiusSm)

    fun target(from: Int): Int = DragOrder.targetIndex(
        heights = currentLines.map { heights[it.id] ?: 0 },
        from = from,
        offset = offset,
        spacing = spacingPx,
        maxIndex = currentLines.lastIndex - 1,
    )

    val from = lines.indexOfFirst { it.id == draggingId }
    val to = if (from >= 0) target(from) else -1
    val draggedHeight = (draggingId?.let { heights[it] } ?: 0) + spacingPx

    Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
        lines.forEachIndexed { index, line ->
            key(line.id) {
                val dragged = line.id == draggingId
                val shift by animateFloatAsState(
                    targetValue = if (from >= 0) DragOrder.shiftOf(index, from, to, draggedHeight) else 0f,
                    animationSpec = AstroMotion.quick(),
                    label = "brickShift",
                )
                val lift by animateFloatAsState(if (dragged) 1f else 0f, AstroMotion.quick(), label = "lift")
                val handle = if (index == lines.lastIndex) null else Modifier.pointerInput(line.id) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            draggingId = line.id
                            offset = 0f
                        },
                        onDrag = { change, amount ->
                            change.consume()
                            offset += amount.y
                        },
                        onDragEnd = {
                            val start = currentLines.indexOfFirst { it.id == line.id }
                            if (start >= 0) currentOnMove(line.id, target(start))
                            draggingId = null
                            offset = 0f
                        },
                        onDragCancel = {
                            draggingId = null
                            offset = 0f
                        },
                    )
                }
                val elevation = with(LocalDensity.current) { PlanningConfig.DRAG_ELEVATION.toPx() }
                Box(
                    modifier = Modifier
                        .onSizeChanged { heights[line.id] = it.height }
                        .zIndex(if (dragged) 1f else 0f)
                        .graphicsLayer {
                            // Hors glissement, aucun décalage : au lâcher, chaque ligne est
                            // déjà à sa nouvelle place, rien ne doit rebondir.
                            translationY = when {
                                dragged -> offset
                                draggingId == null -> 0f
                                else -> shift
                            }
                            val scale = 1f + (PlanningConfig.DRAG_LIFT_SCALE - 1f) * lift
                            scaleX = scale
                            scaleY = scale
                            shadowElevation = elevation * lift
                            this.shape = shape
                        }
                        // Fond opaque sous la brique soulevée : les lignes qui glissent
                        // dessous ne se mélangent pas à son texte.
                        .background(palette.surface.copy(alpha = lift), shape),
                ) {
                    row(line, handle)
                }
            }
        }
    }
}
