package com.astro.app.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme
import java.time.ZoneId
import java.util.Locale

/** Nombre de fuseaux affichés à la fois : la liste complète en compte plusieurs centaines. */
private const val MAX_RESULTS = 60

/**
 * Choix du fuseau horaire.
 *
 * La liste vient de `ZoneId.getAvailableZoneIds()`, donc de la bibliothèque standard :
 * aucun fuseau n'est écrit dans le code, et la base est celle de l'appareil.
 * Un champ de recherche est indispensable — la liste complète est inutilisable telle quelle.
 */
@Composable
fun ZonePickerDialog(
    current: ZoneId,
    title: String,
    searchLabel: String,
    dismissLabel: String,
    onSelect: (ZoneId) -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = AstroTheme.palette
    var query by remember { mutableStateOf("") }
    val zones = remember { ZoneId.getAvailableZoneIds().sorted() }
    val matches = remember(query, zones) {
        val needle = query.trim().lowercase(Locale.ROOT)
        zones.filter { needle.isEmpty() || it.lowercase(Locale.ROOT).contains(needle) }.take(MAX_RESULTS)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(dismissLabel) } },
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(searchLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp).padding(top = 8.dp)) {
                    items(matches) { zone ->
                        Text(
                            text = zone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (zone == current.id) palette.accentSolar else palette.ink,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(ZoneId.of(zone)) }
                                .padding(vertical = 10.dp),
                        )
                    }
                }
            }
        },
    )
}
