package com.astro.app.feature.astrology

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.astronomy.MoonCalculator
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.feature.astrology.components.LunarMonth
import com.astro.app.feature.astrology.components.MoonPanel
import com.astro.app.feature.settings.EphemerisFormat
import java.time.LocalDate
import java.time.YearMonth

/** Nombre de mois affichés dans le calendrier lunaire déplié. */
private const val CALENDAR_MONTHS = 2

/**
 * Page Astrologie, atteinte en appuyant sur la Lune.
 *
 * Les horaires de la Lune vivent ici et non dans les Paramètres : ils appartiennent à
 * l'astre par lequel on y accède. Sans coordonnées renseignées, ils affichent un tiret
 * plutôt que des valeurs calculées pour un lieu arbitraire — le calendrier lunaire,
 * lui, reste disponible, la phase ne dépendant d'aucun lieu.
 */
@Composable
fun AstrologyScreen(
    modifier: Modifier = Modifier,
    viewModel: AstrologyViewModel = viewModel(factory = AstrologyViewModel.Factory),
) {
    val preferences by viewModel.preferences.collectAsState()
    val dimens = AstroTheme.dimens
    val palette = AstroTheme.palette
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val today = remember { LocalDate.now() }
    val unset = stringResource(R.string.home_value_unset)
    var expanded by rememberSaveable { mutableStateOf(false) }

    val events = remember(preferences, today) {
        if (!preferences.hasLocation) null
        else MoonCalculator.eventsFor(today, preferences.zoneId, preferences.latitude!!, preferences.longitude!!)
    }

    AstroScreen(
        overline = stringResource(R.string.astrology_overline),
        title = stringResource(R.string.feature_astrology),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = dimens.spaceXxl),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            MoonPanel(
                overline = stringResource(R.string.settings_moon_title),
                riseLabel = stringResource(R.string.ephemeris_moonrise),
                riseValue = EphemerisFormat.time(events?.moonrise, preferences.zoneId, locale, unset),
                setLabel = stringResource(R.string.ephemeris_moonset),
                setValue = EphemerisFormat.time(events?.moonset, preferences.zoneId, locale, unset),
                expandHint = stringResource(
                    if (expanded) R.string.moon_collapse_hint else R.string.moon_expand_hint,
                ),
                expandLabel = stringResource(R.string.moon_expand_label),
                expanded = expanded,
                onToggle = { expanded = !expanded },
                modifier = Modifier.screenReveal(order = 2),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceLg)) {
                    repeat(CALENDAR_MONTHS) { offset ->
                        LunarMonth(
                            month = YearMonth.from(today).plusMonths(offset.toLong()),
                            zone = preferences.zoneId,
                            locale = locale,
                        )
                    }
                }
            }

            if (!preferences.hasLocation) {
                AstroCard(modifier = Modifier.screenReveal(order = 3)) {
                    Text(
                        text = stringResource(R.string.settings_location_missing),
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.inkFaint,
                    )
                }
            }
        }
    }
}
