package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.manifestation.Manifestation
import com.astro.app.core.speech.SpeechState

/**
 * Une manifestation ouverte : son texte entier, le bouton « Lecture », et les actions
 * Modifier / Supprimer. La suppression demande confirmation : elle est définitive.
 */
@Composable
fun ManifestationReader(
    manifestation: Manifestation,
    speechState: SpeechState,
    onRead: () -> Unit,
    onStop: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    var confirming by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.verticalScroll(rememberScrollState()).padding(bottom = dimens.spaceXxl)) {
        if (manifestation.subject.isNotBlank()) SectionLabel(text = manifestation.subject)
        Text(
            text = manifestation.title.ifBlank { stringResource(R.string.manifestation_untitled) },
            style = MaterialTheme.typography.headlineMedium,
            color = palette.ink,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = dimens.spaceLg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReadButton(state = speechState, onRead = onRead, onStop = onStop)
            Row {
                TextButton(onClick = onEdit) { Text(stringResource(R.string.manifestation_edit), color = palette.accentSolar) }
                TextButton(onClick = { confirming = true }) {
                    Text(stringResource(R.string.manifestation_delete), color = palette.inkMuted)
                }
            }
        }
        Text(text = manifestation.text, style = MaterialTheme.typography.bodyLarge, color = palette.ink)
    }

    if (confirming) {
        AlertDialog(
            onDismissRequest = { confirming = false },
            title = { Text(stringResource(R.string.manifestation_delete_title)) },
            text = { Text(stringResource(R.string.manifestation_delete_message)) },
            confirmButton = {
                TextButton(onClick = { confirming = false; onDelete() }) {
                    Text(stringResource(R.string.manifestation_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirming = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}
