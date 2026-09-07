package com.astro.app.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.settings.KeywordColorRule

/**
 * Règles de couleur par mot.
 *
 * Chaque règle associe un mot à une couleur : si le planning de la journée contient ce
 * mot, la planète prend cette couleur, quel que soit le mode. La première règle qui
 * correspond l'emporte, d'où l'importance de l'ordre affiché.
 */
@Composable
fun KeywordRulesEditor(
    rules: List<KeywordColorRule>,
    fieldLabel: String,
    addLabel: String,
    removeLabel: String,
    emptyLabel: String,
    onAdd: (String) -> Unit,
    onRemove: (Int) -> Unit,
    onRecolor: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    var draft by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        if (rules.isEmpty()) {
            Text(
                text = emptyLabel,
                style = MaterialTheme.typography.bodySmall,
                color = palette.inkFaint,
                modifier = Modifier.padding(bottom = dimens.spaceSm),
            )
        }
        rules.forEachIndexed { index, rule ->
            RuleRow(
                rule = rule,
                removeLabel = removeLabel,
                onRemove = { onRemove(index) },
                onRecolor = { color -> onRecolor(index, color) },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceSm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text(fieldLabel) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.accentSolar,
                    unfocusedBorderColor = palette.surfaceBorder,
                    focusedTextColor = palette.ink,
                    unfocusedTextColor = palette.ink,
                    cursorColor = palette.accentSolar,
                ),
            )
            TextButton(
                onClick = {
                    onAdd(draft)
                    draft = ""
                },
                enabled = draft.isNotBlank(),
            ) {
                Text(text = addLabel, color = if (draft.isNotBlank()) palette.accentSolar else palette.inkFaint)
            }
        }
    }
}

@Composable
private fun RuleRow(
    rule: KeywordColorRule,
    removeLabel: String,
    onRemove: () -> Unit,
    onRecolor: (Int) -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = dimens.spaceXs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(rule.color)),
                )
                Text(
                    text = rule.keyword,
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.ink,
                    modifier = Modifier.padding(start = dimens.spaceSm),
                )
            }
            TextButton(onClick = onRemove) {
                Text(text = removeLabel, color = palette.inkMuted)
            }
        }
        ColorSwatchRow(label = "", selected = rule.color, onSelect = onRecolor)
    }
}
