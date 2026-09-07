package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.astronomy.MoonCalculator
import com.astro.app.core.astronomy.SunCalculator
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.core.settings.UserPreferences
import com.astro.app.feature.settings.EphemerisFormat
import java.time.LocalDate
import java.util.Locale

/**
 * Éphémérides du Soleil et de la Lune pour aujourd'hui.
 *
 * Sans coordonnées renseignées, les deux cartes affichent un état vide : calculer des
 * horaires pour un lieu par défaut donnerait des valeurs fausses présentées comme
 * vraies. Le fuseau ne sert qu'à l'affichage, les coordonnées font le calcul.
 */
@Composable
fun EphemerisSection(preferences: UserPreferences, locale: Locale, modifier: Modifier = Modifier) {
    val dimens = AstroTheme.dimens
    val today = remember { LocalDate.now() }
    val unset = stringResource(R.string.home_value_unset)
    val missing = if (preferences.hasLocation) null else stringResource(R.string.settings_location_missing)

    val sun = remember(preferences, today) {
        if (!preferences.hasLocation) null
        else SunCalculator.eventsFor(today, preferences.latitude!!, preferences.longitude!!)
    }
    val moon = remember(preferences, today) {
        if (!preferences.hasLocation) null
        else MoonCalculator.eventsFor(today, preferences.zoneId, preferences.latitude!!, preferences.longitude!!)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.spaceMd),
    ) {
        EphemerisCard(
            overline = stringResource(R.string.settings_sun_title),
            emptyMessage = missing,
            entries = listOfNotNull(
                sun?.let { EphemerisEntry(stringResource(R.string.ephemeris_sunrise), EphemerisFormat.time(it.sunrise, preferences.zoneId, locale, unset)) },
                sun?.let { EphemerisEntry(stringResource(R.string.ephemeris_noon), EphemerisFormat.time(it.solarNoon, preferences.zoneId, locale, unset)) },
                sun?.let { EphemerisEntry(stringResource(R.string.ephemeris_sunset), EphemerisFormat.time(it.sunset, preferences.zoneId, locale, unset)) },
                sun?.let { EphemerisEntry(stringResource(R.string.ephemeris_day_length), EphemerisFormat.duration(it.dayLengthMinutes, unset)) },
            ),
            modifier = Modifier.screenReveal(order = 5),
        )
        EphemerisCard(
            overline = stringResource(R.string.settings_moon_title),
            emptyMessage = missing,
            entries = listOfNotNull(
                moon?.let { EphemerisEntry(stringResource(R.string.ephemeris_moonrise), EphemerisFormat.time(it.moonrise, preferences.zoneId, locale, unset)) },
                moon?.let { EphemerisEntry(stringResource(R.string.ephemeris_moonset), EphemerisFormat.time(it.moonset, preferences.zoneId, locale, unset)) },
                moon?.let { EphemerisEntry(stringResource(R.string.ephemeris_illumination), EphemerisFormat.percentage(it.illumination)) },
                moon?.let {
                    val phase = if (it.waxing) R.string.ephemeris_waxing else R.string.ephemeris_waning
                    EphemerisEntry(stringResource(R.string.ephemeris_phase), stringResource(phase))
                },
            ),
            modifier = Modifier.screenReveal(order = 6),
        )
    }
}
