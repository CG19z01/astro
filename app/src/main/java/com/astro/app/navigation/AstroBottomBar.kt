package com.astro.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.core.config.FeatureId
import com.astro.app.core.designsystem.components.AstroNavBar
import com.astro.app.core.designsystem.components.AstroNavItem
import com.astro.app.core.settings.FeatureStates

/**
 * Barre de navigation basse (5 entrées sur la maquette).
 *
 * Ce fichier ne fait que traduire le catalogue en éléments de barre : l'apparence
 * appartient entièrement au design system. Une fonctionnalité désactivée disparaît
 * d'elle-même, sans condition écrite ici.
 */
@Composable
fun AstroBottomBar(
    states: Map<FeatureId, Boolean>,
    currentRoute: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = FeatureStates.visibleInBottomBar(states).map { definition ->
        AstroNavItem(
            key = definition.route,
            label = stringResource(definition.titleRes),
            selected = currentRoute == definition.route,
            icon = { tint -> FeatureIcon(id = definition.id, color = tint) },
            onClick = { onSelect(definition.route) },
        )
    }
    AstroNavBar(items = items, modifier = modifier)
}
