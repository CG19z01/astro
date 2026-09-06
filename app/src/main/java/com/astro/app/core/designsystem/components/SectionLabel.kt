package com.astro.app.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme
import java.util.Locale

/**
 * Exergue de section : petites capitales très espacées précédées d'un court filet.
 *
 * C'est le motif qui donne son ton « planche astronomique » à l'application, et le
 * seul endroit où l'on emploie des capitales. Il revient à l'identique sur tous les
 * écrans, ce qui les rattache visuellement les uns aux autres.
 */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(1.dp)
                .background(palette.accentSolar.copy(alpha = 0.8f)),
        )
        Text(
            text = text.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = palette.inkFaint,
        )
    }
}
