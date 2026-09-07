package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.config.PlanetColorDefaults
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.settings.KeywordColorRule
import com.astro.app.core.settings.PlanetColorMode
import com.astro.app.core.settings.PlanetColors
import java.time.format.TextStyle
import java.util.Locale

/**
 * Section « Couleur des planètes ».
 *
 * Deux modes exclusifs, puis les règles de mot qui s'appliquent par-dessus l'un comme
 * l'autre. Seuls les réglages du mode actif sont affichés : montrer les sept jours
 * quand le mode « semaine / week-end » est actif laisserait croire qu'ils comptent.
 */
@Composable
fun PlanetColorSection(
    colors: PlanetColors,
    locale: Locale,
    onChange: (PlanetColors) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroCard(modifier = modifier) {
        SectionLabel(text = stringResource(R.string.settings_planet_colors_title))
        Text(
            text = stringResource(R.string.settings_planet_colors_hint),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
            modifier = Modifier.padding(top = dimens.spaceSm, bottom = dimens.spaceSm),
        )

        PlanetColorMode.entries.forEach { mode ->
            SettingRow(label = stringResource(modeLabel(mode))) {
                AstroSwitch(colors.mode == mode) { checked ->
                    if (checked) onChange(colors.copy(mode = mode))
                }
            }
        }

        when (colors.mode) {
            PlanetColorMode.WEEKDAY_WEEKEND -> {
                ColorSwatchRow(
                    label = stringResource(R.string.settings_planet_weekday),
                    selected = colors.weekday,
                    onSelect = { onChange(colors.copy(weekday = it)) },
                )
                ColorSwatchRow(
                    label = stringResource(R.string.settings_planet_weekend),
                    selected = colors.weekend,
                    onSelect = { onChange(colors.copy(weekend = it)) },
                )
            }

            PlanetColorMode.PER_DAY -> {
                PlanetColorDefaults.PER_DAY.keys.forEach { day ->
                    ColorSwatchRow(
                        label = day.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                            .replaceFirstChar { it.titlecase(locale) },
                        selected = colors.perDay[day] ?: PlanetColorDefaults.PER_DAY.getValue(day),
                        onSelect = { color ->
                            onChange(colors.copy(perDay = colors.perDay + (day to color)))
                        },
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.settings_planet_keywords_title),
            style = MaterialTheme.typography.bodyMedium,
            color = palette.inkMuted,
            modifier = Modifier.padding(top = dimens.spaceLg),
        )
        Text(
            text = stringResource(R.string.settings_planet_keywords_hint),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
            modifier = Modifier.padding(top = dimens.spaceXs, bottom = dimens.spaceSm),
        )
        KeywordRulesEditor(
            rules = colors.keywordRules,
            fieldLabel = stringResource(R.string.settings_planet_keyword_field),
            addLabel = stringResource(R.string.settings_planet_keyword_add),
            removeLabel = stringResource(R.string.settings_planet_keyword_remove),
            emptyLabel = stringResource(R.string.settings_planet_keyword_empty),
            onAdd = { word ->
                val rule = KeywordColorRule(word.trim(), PlanetColorDefaults.KEYWORD)
                onChange(colors.copy(keywordRules = colors.keywordRules + rule))
            },
            onRemove = { index ->
                onChange(colors.copy(keywordRules = colors.keywordRules.filterIndexed { i, _ -> i != index }))
            },
            onRecolor = { index, color ->
                val updated = colors.keywordRules.mapIndexed { i, rule ->
                    if (i == index) rule.copy(color = color) else rule
                }
                onChange(colors.copy(keywordRules = updated))
            },
        )
    }
}

private fun modeLabel(mode: PlanetColorMode): Int = when (mode) {
    PlanetColorMode.WEEKDAY_WEEKEND -> R.string.settings_planet_mode_week
    PlanetColorMode.PER_DAY -> R.string.settings_planet_mode_day
}
