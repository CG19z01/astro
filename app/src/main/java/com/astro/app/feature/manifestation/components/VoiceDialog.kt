package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.speech.SpeechVoice

/**
 * Choix de la voix de lecture, parmi les voix françaises installées.
 *
 * Android ne dit pas si une voix est féminine ou masculine : chacune s'écoute avant
 * d'être choisie (« Écouter »). La première ligne, « Voix par défaut », laisse le
 * moteur décider.
 */
@Composable
fun VoiceDialog(
    voices: List<SpeechVoice>,
    selected: String?,
    onSelect: (String?) -> Unit,
    onPreview: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val dimens = AstroTheme.dimens
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) } },
        title = { Text(stringResource(R.string.manifestation_voice_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = dimens.touchTarget * 8)) {
                item { VoiceRow(stringResource(R.string.manifestation_voice_default), null, selected, onSelect, onPreview) }
                items(voices, key = { it.name }) { voice ->
                    VoiceRow(describe(voice), voice.name, selected, onSelect, onPreview)
                }
            }
        },
    )
}

@Composable
private fun describe(voice: SpeechVoice): String {
    val access = if (voice.needsNetwork) R.string.manifestation_voice_online else R.string.manifestation_voice_offline
    return "${voice.name} · ${stringResource(access)}"
}

@Composable
private fun VoiceRow(
    label: String,
    name: String?,
    selected: String?,
    onSelect: (String?) -> Unit,
    onPreview: (String?) -> Unit,
) {
    val palette = AstroTheme.palette
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onSelect(name) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = name == selected, onClick = { onSelect(name) })
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.ink,
            modifier = Modifier.weight(1f).padding(end = AstroTheme.dimens.spaceSm),
        )
        TextButton(onClick = { onPreview(name) }) {
            Text(stringResource(R.string.manifestation_voice_preview), color = palette.accentSolar)
        }
    }
}
