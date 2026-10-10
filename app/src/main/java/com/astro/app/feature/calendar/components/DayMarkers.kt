package com.astro.app.feature.calendar.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.astro.app.core.config.CalendarConfig
import com.astro.app.core.designsystem.celestial.MoonPhaseGlyph
import com.astro.app.feature.calendar.DayMarks

/**
 * Les deux marqueurs d'un jour, communs à toutes les vues.
 *
 * **Pleine lune** : le disque de phase du calendrier lunaire, entièrement éclairé. On
 * reprend le glyphe existant plutôt que d'en dessiner un autre : c'est la même Lune
 * que sur la page Astrologie, donc reconnue sans explication.
 *
 * **Bulle de planning** : un point plein de la couleur de la planète-journée. Petit à
 * dessein — il signale qu'une note existe, il ne doit pas prendre le pas sur la date.
 */
@Composable
fun FullMoonMark(modifier: Modifier = Modifier, size: Dp = CalendarConfig.FULL_MOON_MARK_SIZE) {
    MoonPhaseGlyph(size = size, illumination = 1.0, waxing = true, modifier = modifier)
}

@Composable
fun PlanningBubble(argb: Int, modifier: Modifier = Modifier) {
    val color = Color(argb)
    Canvas(modifier = modifier.size(CalendarConfig.PLANNING_BUBBLE_SIZE)) {
        drawCircle(color = color)
    }
}

/** La bulle d'un jour si celui-ci en porte une ; rien sinon. */
@Composable
fun PlanningBubbleFor(marks: DayMarks, modifier: Modifier = Modifier) {
    marks.planningColor?.let { PlanningBubble(argb = it, modifier = modifier) }
}
