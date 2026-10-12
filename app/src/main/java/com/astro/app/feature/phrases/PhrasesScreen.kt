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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.feature.phrases.components.PhraseHistoryItem
import com.astro.app.feature.phrases.components.PhraseImportCard
import kotlinx.coroutines.launch

/**
 * Historique des phrases.
 *
 * En tête, la liste de l'utilisateur et son import ; dessous, la phrase montrée
 * chaque jour, la plus récente en premier. Les phrases viennent toutes du fichier de
 * l'utilisateur : l'application n'en fournit aucune.
 */
@Composable
fun PhrasesScreen(
    modifier: Modifier = Modifier,
    viewModel: PhrasesViewModel = viewModel(factory = PhrasesViewModel.Factory),
) {
    val dimens = AstroTheme.dimens
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val count by viewModel.count.collectAsState()
    val history by viewModel.history.collectAsState()
    val status by viewModel.status.collectAsState()
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
    ) {
        LazyColumn(
            modifier = Modifier.screenReveal(order = 2),
            contentPadding = PaddingValues(bottom = dimens.spaceXxl),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceLg),
        ) {
            item {
                PhraseImportCard(count = count, status = status, onImport = { picker.launch(arrayOf("*/*")) })
            }
            items(history, key = { it.first.toEpochDay() }) { (date, phrase) ->
                PhraseHistoryItem(date = date, phrase = phrase, locale = locale)
            }
        }
    }
}
