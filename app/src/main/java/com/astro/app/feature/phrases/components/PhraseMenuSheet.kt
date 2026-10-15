package com.astro.app.feature.phrases.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroChip

/** Les onglets du menu de la page Phrases. */
enum class PhraseMenuTab(val label: Int) {
    WISHES(R.string.phrases_tab_wishes),
    FAVORITES(R.string.phrases_tab_favorites),
    LIST(R.string.phrases_tab_list),
}

/**
 * Menu de la page Phrases, ouvert par le cercle à côté du titre : une feuille qui monte
 * du bas, à trois onglets — « Mes phrases » (genre et thèmes), « Favoris », « Ma liste »
 * (import du fichier). Chaque choix s'applique aussitôt ; on referme en glissant.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseMenuSheet(onDismiss: () -> Unit, content: @Composable (PhraseMenuTab) -> Unit) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    var tab by rememberSaveable { mutableStateOf(PhraseMenuTab.WISHES) }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = palette.skyTop, contentColor = palette.ink) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = dimens.spaceXl)
                .navigationBarsPadding(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
                modifier = Modifier.padding(bottom = dimens.spaceLg),
            ) {
                PhraseMenuTab.entries.forEach { entry ->
                    AstroChip(stringResource(entry.label), entry == tab, onClick = { tab = entry })
                }
            }
            content(tab)
        }
    }
}
