package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.manifestation.Manifestation

/**
 * Liste des manifestations, la plus récente en premier, avec le filtre par sujet.
 *
 * Le bas de liste laisse la place au bouton « + », posé par-dessus.
 */
@Composable
fun ManifestationList(
    shown: List<Manifestation>,
    isEmpty: Boolean,
    subjects: List<String>,
    subject: String?,
    onFilter: (String?) -> Unit,
    onOpen: (Long) -> Unit,
    onVoices: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = dimens.touchTarget * 2 + dimens.spaceXl),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceMd),
    ) {
        item {
            TextButton(onClick = onVoices) {
                Text(
                    text = stringResource(R.string.manifestation_voice),
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.accentSolar,
                )
            }
        }
        if (subjects.isNotEmpty()) {
            item {
                SubjectChips(
                    allLabel = stringResource(R.string.manifestation_filter_all),
                    subjects = subjects,
                    selected = subject,
                    onSelect = onFilter,
                )
            }
        }
        if (isEmpty) {
            item {
                Text(
                    text = stringResource(R.string.manifestation_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.inkMuted,
                )
            }
        }
        items(shown, key = { it.id }) { manifestation ->
            ManifestationCard(
                manifestation = manifestation,
                untitled = stringResource(R.string.manifestation_untitled),
                onClick = { onOpen(manifestation.id) },
            )
        }
    }
}
