package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.astro.app.R
import com.astro.app.core.astronomy.SunCalculator
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.celestial.SunOrb
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.settings.UserPreferences
import com.astro.app.feature.settings.EphemerisFormat
import java.time.LocalDate
import java.util.Locale

/** Taille du Soleil dans la carte. Assez grande pour que la couronne se lise. */
private val ORB_SIZE = 132.dp

/**
 * Horaires du Soleil, atteints en appuyant sur le Soleil de la page d'accueil.
 *
 * Le Soleil affiché est exactement celui de la page d'accueil — même composant, même
 * matière. Les horaires sont posés à côté de lui et non dessus : du texte sur une
 * texture perd en lisibilité.
 *
 * Les horaires de la Lune ne sont plus ici : ils appartiennent à la page atteinte par
 * la Lune, pas à celle atteinte par le Soleil.
 */
@Composable
fun EphemerisSection(preferences: UserPreferences, locale: Locale, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val today = remember { LocalDate.now() }
    val unset = stringResource(R.string.home_value_unset)

    val events = remember(preferences, today) {
        if (!preferences.hasLocation) null
        else SunCalculator.eventsFor(today, preferences.latitude!!, preferences.longitude!!)
    }

    AstroCard(modifier = modifier) {
        SectionLabel(text = stringResource(R.string.settings_sun_title))
        if (events == null) {
            Text(
                text = stringResource(R.string.settings_location_missing),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkFaint,
                modifier = Modifier.padding(top = dimens.spaceMd),
            )
            return@AstroCard
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            SunOrb(size = ORB_SIZE)
            Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceSm)) {
                TimeLine(
                    label = stringResource(R.string.ephemeris_sunrise),
                    value = EphemerisFormat.time(events.sunrise, preferences.zoneId, locale, unset),
                )
                TimeLine(
                    label = stringResource(R.string.ephemeris_sunset),
                    value = EphemerisFormat.time(events.sunset, preferences.zoneId, locale, unset),
                )
            }
        }
    }
}

@Composable
private fun TimeLine(label: String, value: String) {
    val palette = AstroTheme.palette
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = palette.inkMuted)
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = palette.ink)
    }
}
