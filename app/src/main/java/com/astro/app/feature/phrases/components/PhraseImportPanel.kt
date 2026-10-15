package com.astro.app.feature.phrases.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.phrases.PhraseImport
import com.astro.app.feature.phrases.ImportStatus

/**
 * Onglet « Ma liste » du menu : combien de phrases sont importées, et le bouton pour
 * (ré)importer. Retiré de la page le 2026-10-15, à la demande de l'utilisateur.
 */
@Composable
fun PhraseImportPanel(count: Int, status: ImportStatus?, onImport: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Column(modifier = modifier) {
        Text(
            text = if (count == 0) stringResource(R.string.phrases_list_empty)
            else pluralStringResource(R.plurals.phrases_count, count, count),
            style = MaterialTheme.typography.titleMedium,
            color = palette.ink,
        )
        Text(
            text = stringResource(R.string.phrases_format_hint),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
        status?.let {
            Text(
                text = statusText(it),
                style = MaterialTheme.typography.bodySmall,
                color = if (it is ImportStatus.Imported) palette.accentLunar else palette.accentSolar,
                modifier = Modifier.padding(top = dimens.spaceSm),
            )
        }
        TextButton(onClick = onImport, modifier = Modifier.padding(top = dimens.spaceXs)) {
            Text(
                text = stringResource(R.string.phrases_import),
                style = MaterialTheme.typography.labelLarge,
                color = palette.accentSolar,
            )
        }
    }
}

@Composable
private fun statusText(status: ImportStatus): String = when (status) {
    is ImportStatus.Imported -> pluralStringResource(R.plurals.phrases_imported, status.count, status.count)
    ImportStatus.TooLarge -> stringResource(R.string.phrases_error_too_large)
    ImportStatus.Unreadable -> stringResource(R.string.phrases_error_unreadable)
    is ImportStatus.Refused -> when (status.failure.reason) {
        PhraseImport.Reason.NOT_JSON -> stringResource(R.string.phrases_error_not_json)
        PhraseImport.Reason.NOT_A_LIST -> stringResource(R.string.phrases_error_not_list)
        PhraseImport.Reason.EMPTY -> stringResource(R.string.phrases_error_empty)
        PhraseImport.Reason.MISSING_TEXT ->
            stringResource(R.string.phrases_error_missing_text, status.failure.position ?: 0)
        PhraseImport.Reason.UNKNOWN_GENDER ->
            stringResource(R.string.phrases_error_unknown_gender, status.failure.position ?: 0)
    }
}
