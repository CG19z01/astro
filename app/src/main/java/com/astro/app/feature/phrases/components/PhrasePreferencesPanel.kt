package com.astro.app.feature.phrases.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroChip
import com.astro.app.core.phrases.PhraseWishes
import com.astro.app.core.phrases.ReaderGender

/**
 * « Mes phrases » : à qui s'adressent les phrases et sur quels thèmes. Ouvert par le
 * bouton « burger » à côté du titre de la page (demande du 2026-10-15).
 *
 * « Autre » — les phrases neutres — est toujours inclus ; Masculin ou Féminin s'y
 * ajoutent. Sans l'un des deux, seules les neutres sont proposées : une phrase accordée
 * au mauvais genre ne s'adresse pas à la personne.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhrasePreferencesPanel(
    wishes: PhraseWishes,
    themes: List<String>,
    onGender: (ReaderGender) -> Unit,
    onToggleTheme: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val spacing = Arrangement.spacedBy(dimens.spaceSm)
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.phrases_gender_question),
            style = MaterialTheme.typography.titleMedium,
            color = palette.ink,
            modifier = Modifier.padding(bottom = dimens.spaceSm),
        )
        FlowRow(horizontalArrangement = spacing, verticalArrangement = spacing) {
            GENDERS.forEach { (gender, label) ->
                AstroChip(stringResource(label), wishes.reader == gender, onClick = { onGender(gender) })
            }
            // « Autre » (les phrases neutres) est inclus dans tous les cas : toujours allumé,
            // il ne se décoche pas.
            AstroChip(stringResource(R.string.phrases_gender_other), active = true, onClick = {})
        }
        Text(
            text = stringResource(R.string.phrases_gender_hint),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
        if (themes.isNotEmpty()) {
            Text(
                text = stringResource(R.string.phrases_themes_question),
                style = MaterialTheme.typography.titleMedium,
                color = palette.ink,
                modifier = Modifier.padding(top = dimens.spaceLg, bottom = dimens.spaceSm),
            )
            FlowRow(horizontalArrangement = spacing, verticalArrangement = spacing) {
                AstroChip(stringResource(R.string.phrases_themes_all), wishes.themes.isEmpty(), onClick = { onToggleTheme(null) })
                themes.forEach { theme ->
                    val active = wishes.themes.any { it.equals(theme, ignoreCase = true) }
                    AstroChip(theme, active, onClick = { onToggleTheme(theme) })
                }
            }
        }
    }
}

private val GENDERS = listOf(
    ReaderGender.MASCULINE to R.string.phrases_gender_masculine,
    ReaderGender.FEMININE to R.string.phrases_gender_feminine,
)
