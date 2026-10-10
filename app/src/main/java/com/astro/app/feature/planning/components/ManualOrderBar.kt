package com.astro.app.feature.planning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme

/**
 * Signale qu'une journée est en ordre manuel, et permet d'en sortir.
 *
 * N'apparaît qu'après un déplacement : en ordre automatique, la page reste sans texte
 * d'aide, comme demandé le 2026-10-10.
 */
@Composable
fun ManualOrderBar(onReset: () -> Unit) {
    val palette = AstroTheme.palette
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.planning_order_manual),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
        )
        TextButton(onClick = onReset) {
            Text(stringResource(R.string.planning_order_reset), color = palette.accentSolar)
        }
    }
}
