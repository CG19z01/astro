package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.manifestation.Manifestation

/** Une manifestation dans la liste : son sujet, son titre et le début de son texte. */
@Composable
fun ManifestationCard(manifestation: Manifestation, untitled: String, onClick: () -> Unit) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    AstroCard(modifier = Modifier.clip(RoundedCornerShape(dimens.radiusLg)).clickable(onClick = onClick)) {
        if (manifestation.subject.isNotBlank()) SectionLabel(text = manifestation.subject)
        Text(
            text = manifestation.title.ifBlank { untitled },
            style = MaterialTheme.typography.headlineSmall,
            color = if (manifestation.title.isBlank()) palette.inkFaint else palette.ink,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
        if (manifestation.text.isNotBlank()) {
            Text(
                text = manifestation.text,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = dimens.spaceXs),
            )
        }
    }
}
