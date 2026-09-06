package com.astro.app.feature.calendar

import android.os.SystemClock
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.onHorizontalSwipe
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.core.time.SwipeBurstDetector
import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeScale

/**
 * Page Calendrier.
 *
 * Les quatre temporalités sont affichées en permanence comme un cadran : on voit
 * d'un coup d'œil où l'on se trouve et ce qui vient après, ce qu'un simple libellé
 * ne dirait pas. Le niveau courant s'allume, les autres restent en retrait.
 *
 * Le retour au mode Jour à chaque entrée est garanti par la navigation : quitter
 * cet écran détruit son entrée de pile, donc l'état mémorisé ci-dessous.
 */
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    var scale by rememberSaveable { mutableStateOf(TimeScale.INITIAL) }
    val detector = remember { SwipeBurstDetector() }
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroScreen(
        overline = stringResource(R.string.calendar_overline),
        title = stringResource(R.string.feature_calendar),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().screenReveal(order = 2),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            TimeScale.entries.forEach { entry ->
                ScaleChip(entry = entry, active = entry == scale)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimens.spaceLg, bottom = dimens.spaceXxl)
                // Salve de 3 swipes rapides : ce geste CHANGE de temporalité. Il est
                // distinct du swipe simple de la Home, qui navigue dans la temporalité.
                .onHorizontalSwipe(key = detector) { direction ->
                    if (detector.onSwipe(direction, SystemClock.uptimeMillis())) {
                        scale = when (direction) {
                            SwipeDirection.FORWARD -> scale.next()
                            SwipeDirection.BACKWARD -> scale.previous()
                        }
                    }
                },
        ) {
            AstroCard(modifier = Modifier.fillMaxSize().screenReveal(order = 3)) {
                Text(
                    text = stringResource(scale.labelRes),
                    style = MaterialTheme.typography.headlineLarge,
                    color = palette.ink,
                )
                Text(
                    text = stringResource(R.string.calendar_swipe_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.inkFaint,
                    modifier = Modifier.padding(top = dimens.spaceSm),
                )
            }
        }
    }
}

@Composable
private fun ScaleChip(entry: TimeScale, active: Boolean) {
    val palette = AstroTheme.palette
    val color by animateColorAsState(
        targetValue = if (active) palette.accentSolar else palette.inkFaint,
        animationSpec = AstroMotion.quick(),
        label = "scaleChip",
    )
    Text(
        text = stringResource(entry.labelRes),
        style = MaterialTheme.typography.labelLarge,
        color = color,
    )
}
