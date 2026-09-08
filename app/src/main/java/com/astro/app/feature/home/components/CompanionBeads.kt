package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.astro.app.core.config.HomeLayoutConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.settings.PlanetColorResolver
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import com.astro.app.feature.home.CompanionTitle
import java.time.LocalDate
import java.util.Locale

/** Atténuation de ces planètes : elles accompagnent, elles ne disputent pas la vedette. */
private const val COMPANION_EMPHASIS = 0.42f

/**
 * Planètes d'accompagnement : deux journées à venir, plus petites que la bille
 * secondaire et posées en haut de l'écran.
 *
 * Elles ne portent que deux informations : le jour et le début du planning, tronqué.
 * C'est délibéré — leur surface visible est réduite, et y entasser davantage rendrait
 * l'ensemble illisible. Un titre coupé est assumé.
 *
 * Elles restent cliquables et mènent au planning de leur propre date.
 */
@Composable
fun CompanionBeads(
    width: Dp,
    height: Dp,
    cursor: TimeCursor,
    locale: Locale,
    plannings: List<String>,
    planetColors: PlanetColors,
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette

    Box(modifier = modifier.fillMaxSize()) {
        HomeLayoutConfig.companionBeads.forEachIndexed { index, spec ->
            val offset = HomeLayoutConfig.COMPANION_OFFSETS.getOrNull(index) ?: return@forEachIndexed
            val target = cursor.shifted(offset)
            val text = plannings.getOrElse(index) { "" }
            val bounds = spec.bounds(width, height)
            val (contentX, contentY) = HomeLayoutConfig.COMPANION_CONTENT[index]

            DayBead(
                size = bounds.size,
                emphasis = COMPANION_EMPHASIS,
                seed = target.anchor.toEpochDay().toInt(),
                tint = Color(
                    PlanetColorResolver.colorFor(target.anchor.dayOfWeek, text, planetColors),
                ),
                contentDescription = PeriodLabels.title(target, locale),
                onClick = { onOpenPlanning(target.anchor) },
                modifier = Modifier.offset(x = bounds.x, y = bounds.y),
            ) {
                Column(
                    modifier = Modifier
                        .offset(x = bounds.size * contentX, y = bounds.size * contentY)
                        // Assez large pour un nom de jour entier : « Dimanche » y était
                        // tronqué, ce qui donnait une planète à l'étiquette illisible.
                        .widthIn(max = bounds.size * 0.52f),
                ) {
                    Text(
                        text = PeriodLabels.title(target, locale),
                        style = MaterialTheme.typography.labelLarge,
                        color = palette.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = CompanionTitle.of(text),
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.inkFaint,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
