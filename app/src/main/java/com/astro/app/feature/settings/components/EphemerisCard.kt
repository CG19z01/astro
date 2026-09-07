package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel

/** Une valeur d'éphéméride : un libellé et une heure, ou un tiret si elle n'existe pas. */
data class EphemerisEntry(val label: String, val value: String)

/**
 * Carte d'éphémérides : les horaires d'un astre.
 *
 * Les valeurs sont fournies déjà formatées par l'appelant : cette carte ne calcule rien
 * et ne connaît ni fuseau ni coordonnées. Elle affiche un état vide explicite plutôt
 * que des horaires pour un lieu arbitraire.
 */
@Composable
fun EphemerisCard(
    overline: String,
    entries: List<EphemerisEntry>,
    emptyMessage: String?,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroCard(modifier = modifier) {
        SectionLabel(text = overline)
        if (emptyMessage != null) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkFaint,
                modifier = Modifier.padding(top = dimens.spaceMd),
            )
            return@AstroCard
        }
        Column(
            modifier = Modifier.padding(top = dimens.spaceMd),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceSm),
        ) {
            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = entry.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.inkMuted,
                    )
                    Text(
                        text = entry.value,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.ink,
                    )
                }
            }
        }
    }
}
