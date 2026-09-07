package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel

/**
 * Réglage du lieu d'observation et du fuseau d'affichage.
 *
 * Les deux sont distincts et le rappel en sous-titre l'explique : les horaires des
 * astres se calculent à partir des coordonnées, jamais du fuseau. Deux villes du même
 * fuseau mais à des longitudes différentes n'ont pas le même lever de soleil.
 */
@Composable
fun LocationSection(
    overline: String,
    hint: String,
    latitudeLabel: String,
    longitudeLabel: String,
    zoneLabel: String,
    latitude: String,
    longitude: String,
    zoneId: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onPickZone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroCard(modifier = modifier) {
        SectionLabel(text = overline)
        Text(
            text = hint,
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceMd),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            CoordinateField(latitudeLabel, latitude, onLatitudeChange, Modifier.weight(1f))
            CoordinateField(longitudeLabel, longitude, onLongitudeChange, Modifier.weight(1f))
        }
        SettingRow(
            label = zoneLabel,
            description = zoneId,
            modifier = Modifier.padding(top = dimens.spaceSm),
        ) {
            TextButton(onClick = onPickZone) {
                Text(text = zoneId.substringAfterLast('/'), color = palette.accentSolar)
            }
        }
    }
}

/**
 * Champ de coordonnée.
 *
 * Le texte saisi est mémorisé à part de la valeur enregistrée. Lier directement le
 * champ au nombre stocké rend la saisie impossible : à peine « 48. » tapé, la valeur
 * est reparsée puis réaffichée « 48.0 », et le point disparaît sous les doigts.
 *
 * Le champ n'est réécrit depuis l'extérieur que si la valeur enregistrée diffère
 * réellement de ce qui est affiché, ce qui laisse la frappe tranquille.
 */
@Composable
private fun CoordinateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    var text by rememberSaveable { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (text.toDoubleOrNull() != value.toDoubleOrNull()) text = value
    }
    OutlinedTextField(
        value = text,
        onValueChange = { typed ->
            text = typed
            onValueChange(typed)
        },
        label = { Text(label) },
        singleLine = true,
        // Le clavier décimal accepte le signe : les latitudes sud et longitudes ouest
        // sont négatives, les refuser exclurait la moitié du globe.
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.accentSolar,
            unfocusedBorderColor = palette.surfaceBorder,
            focusedTextColor = palette.ink,
            unfocusedTextColor = palette.ink,
            cursorColor = palette.accentSolar,
        ),
    )
}
