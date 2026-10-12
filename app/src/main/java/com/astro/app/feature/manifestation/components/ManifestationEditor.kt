package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * Création ou modification : titre, sujet (texte libre) et texte.
 *
 * Rien n'est écrit avant « Enregistrer » : « Annuler » ou Retour abandonnent la saisie.
 */
@Composable
fun ManifestationEditor(
    draft: Manifestation,
    onChange: (Manifestation) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Column(
        modifier = modifier.imePadding().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceMd),
    ) {
        ManifestationField(draft.title, stringResource(R.string.manifestation_field_title), { onChange(draft.copy(title = it)) })
        ManifestationField(draft.subject, stringResource(R.string.manifestation_field_subject), { onChange(draft.copy(subject = it)) })
        ManifestationField(
            value = draft.text,
            label = stringResource(R.string.manifestation_field_text),
            onValueChange = { onChange(draft.copy(text = it)) },
            singleLine = false,
            minLines = 6,
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.action_cancel), color = palette.inkMuted)
            }
            TextButton(onClick = onSave, enabled = !draft.isBlank) {
                Text(
                    text = stringResource(R.string.manifestation_save),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (draft.isBlank) palette.inkFaint else palette.accentSolar,
                )
            }
        }
    }
}
