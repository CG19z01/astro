package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.speech.SpeechState

/**
 * Le bouton « Lecture », qui devient « Arrêter » pendant la lecture.
 *
 * Il dit pourquoi il ne fait rien quand la lecture est impossible, plutôt que de
 * rester muet : moteur qui démarre, ou aucune voix française sur le téléphone.
 */
@Composable
fun ReadButton(state: SpeechState, onRead: () -> Unit, onStop: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val shape = RoundedCornerShape(dimens.radiusMd)
    val label = when (state) {
        SpeechState.STARTING -> R.string.manifestation_speech_starting
        SpeechState.READY -> R.string.manifestation_read
        SpeechState.SPEAKING -> R.string.manifestation_stop
        SpeechState.UNAVAILABLE -> R.string.manifestation_speech_unavailable
    }
    val active = state == SpeechState.READY || state == SpeechState.SPEAKING
    Text(
        text = stringResource(label),
        style = MaterialTheme.typography.titleMedium,
        color = if (active) palette.surface else palette.inkMuted,
        modifier = modifier
            .clip(shape)
            .background(if (active) palette.accentSolar else palette.surface)
            .clickable(enabled = active, role = Role.Button) {
                if (state == SpeechState.SPEAKING) onStop() else onRead()
            }
            .padding(horizontal = dimens.spaceLg, vertical = dimens.spaceMd),
    )
}
