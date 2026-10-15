package com.astro.app.feature.phrases

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.feature.phrases.components.FavoritesPanel
import com.astro.app.feature.phrases.components.PhraseImportPanel
import com.astro.app.feature.phrases.components.PhraseItem
import com.astro.app.feature.phrases.components.PhraseMenuSheet
import com.astro.app.feature.phrases.components.PhraseMenuTab
import com.astro.app.feature.phrases.components.PhrasePreferencesPanel
import com.astro.app.core.designsystem.components.MenuButton
import kotlinx.coroutines.launch

/**
 * Historique des phrases.
 *
 * La page ne montre que les phrases du jour passées, la plus récente en premier. Le
 * cercle à côté du titre ouvre le menu : genre et thèmes, favoris, import de la liste. Les phrases viennent toutes du fichier de
 * l'utilisateur : l'application n'en fournit aucune.
 */
@Composable
fun PhrasesScreen(
    modifier: Modifier = Modifier,
    viewModel: PhrasesViewModel = viewModel(factory = PhrasesViewModel.Factory),
) {
    val dimens = AstroTheme.dimens
    val count by viewModel.count.collectAsState()
    val history by viewModel.history.collectAsState()
    val status by viewModel.status.collectAsState()
    val wishes by viewModel.wishes.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favoriteTexts = favorites.map { it.text }.toSet()
    val themes by viewModel.themes.collectAsState()
    // Feuille « Mes phrases » ouverte. Survit à la rotation.
    var choosing by rememberSaveable { mutableStateOf(false) }
    val resolver = LocalContext.current.contentResolver
    val reader = remember(resolver) { PhraseFileReader(resolver) }
    val scope = rememberCoroutineScope()
    // Tous les types sont proposés : beaucoup de gestionnaires de fichiers ne
    // reconnaissent pas un .json comme tel. Le contenu est vérifié à la lecture.
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) scope.launch { viewModel.onFileRead(reader.read(uri)) }
    }

    AstroScreen(
        overline = stringResource(R.string.phrases_overline),
        title = stringResource(R.string.feature_phrases),
        modifier = modifier,
        action = { MenuButton(label = stringResource(R.string.phrases_menu), onClick = { choosing = true }) },
    ) {
        LazyColumn(
            modifier = Modifier.screenReveal(order = 2),
            contentPadding = PaddingValues(bottom = dimens.spaceXxl),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceLg),
        ) {
            // Les phrases montrées, la plus récente en premier — sans leur date.
            items(history, key = { it.first.toEpochDay() }) { (_, phrase) ->
                PhraseItem(
                    phrase = phrase,
                    favorite = phrase.text in favoriteTexts,
                    onToggleFavorite = { viewModel.onToggleFavorite(phrase) },
                )
            }
        }
    }

    if (choosing) {
        PhraseMenuSheet(onDismiss = { choosing = false }) { tab ->
            when (tab) {
                PhraseMenuTab.WISHES -> PhrasePreferencesPanel(
                    wishes = wishes,
                    themes = themes,
                    onGender = viewModel::onReaderGender,
                    onToggleTheme = viewModel::onToggleTheme,
                )
                PhraseMenuTab.FAVORITES -> FavoritesPanel(favorites, viewModel::onToggleFavorite)
                PhraseMenuTab.LIST -> PhraseImportPanel(count, status, onImport = { picker.launch(arrayOf("*/*")) })
            }
        }
    }
}
