package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import com.astro.app.core.config.DayWheelConfig

/**
 * Messages d'une planète, qui défilent sous le doigt, sans fenêtre supplémentaire.
 *
 * La zone a la hauteur de [lines] lignes : au-delà, on fait glisser verticalement.
 * Un fondu sur le bord concerné signale qu'il reste du texte au-dessus ou au-dessous,
 * sans flèche ni barre qui encombreraient la planète.
 *
 * Le défilement ne consomme que les gestes verticaux : le swipe horizontal de l'accueil
 * (navigation dans le temps) et l'appui qui ouvre le planning continuent de passer.
 */
@Composable
fun PlanetMessages(
    text: String,
    lines: Int,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()
    val height = with(LocalDensity.current) { (style.lineHeight * lines).toDp() }
    val fade = with(LocalDensity.current) { (style.lineHeight * DayWheelConfig.MESSAGE_FADE_LINES).toPx() }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
            .heightIn(max = height)
            // Calque séparé : le fondu efface le texte lui-même, pas la planète dessous.
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                if (scroll.canScrollBackward) {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black), endY = fade),
                        blendMode = BlendMode.DstIn,
                    )
                }
                if (scroll.canScrollForward) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.Black, Color.Transparent),
                            startY = size.height - fade,
                            endY = size.height,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                }
            }
            .verticalScroll(scroll),
    )
}
