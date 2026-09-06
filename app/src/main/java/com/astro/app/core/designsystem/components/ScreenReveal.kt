package com.astro.app.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroMotion

/**
 * Apparition à l'entrée d'un écran : fondu accompagné d'une légère montée.
 *
 * [order] décale le départ, ce qui fait apparaître les éléments en cascade plutôt
 * que d'un bloc. Le mouvement est court — quelques dp — car une longue translation
 * donne l'impression que l'interface se met en place lentement.
 */
@Composable
fun Modifier.screenReveal(order: Int = 0, rise: Dp = 14.dp): Modifier {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(
            durationMillis = AstroMotion.REVEAL_MILLIS,
            delayMillis = order * AstroMotion.STAGGER_MILLIS,
            easing = AstroMotion.Emphasized,
        ),
        label = "reveal",
    )

    return this
        .alpha(progress)
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val offsetY = (rise.toPx() * (1f - progress)).toInt()
            layout(placeable.width, placeable.height) { placeable.place(0, offsetY) }
        }
}
