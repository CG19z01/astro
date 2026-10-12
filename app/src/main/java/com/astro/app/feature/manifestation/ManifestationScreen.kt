package com.astro.app.feature.manifestation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.feature.manifestation.components.AddButton
import com.astro.app.feature.manifestation.components.ManifestationEditor
import com.astro.app.feature.manifestation.components.ManifestationList
import com.astro.app.feature.manifestation.components.ManifestationReader
import com.astro.app.feature.manifestation.components.VoiceDialog

/**
 * Page Manifestation : la liste, la lecture d'une manifestation et son édition.
 *
 * Retour remonte d'un cran (édition → lecture → liste) avant de ramener à l'accueil :
 * sans cela, un Retour pendant la saisie ferait perdre la page entière.
 */
@Composable
fun ManifestationScreen(
    modifier: Modifier = Modifier,
    viewModel: ManifestationViewModel = viewModel(factory = ManifestationViewModel.Factory),
) {
    val dimens = AstroTheme.dimens
    val page by viewModel.page.collectAsState()
    val all by viewModel.all.collectAsState()
    val shown by viewModel.shown.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val subject by viewModel.subject.collectAsState()
    val speechState by viewModel.speechState.collectAsState()
    val voices by viewModel.voices.collectAsState()
    val voiceName by viewModel.voiceName.collectAsState()
    var choosingVoice by rememberSaveable { mutableStateOf(false) }
    val sample = stringResource(R.string.manifestation_voice_sample)

    BackHandler(enabled = page != ManifestationPage.List, onBack = viewModel::onBack)

    Box(modifier = modifier.fillMaxSize()) {
        AstroScreen(
            overline = stringResource(R.string.manifestation_overline),
            title = stringResource(R.string.feature_manifestation),
        ) {
            val content = Modifier.screenReveal(order = 2)
            when (val current = page) {
                ManifestationPage.List -> ManifestationList(
                    shown = shown,
                    isEmpty = all.isEmpty(),
                    subjects = subjects,
                    subject = subject,
                    onFilter = viewModel::onFilter,
                    onOpen = viewModel::onOpen,
                    onVoices = { choosingVoice = true },
                    modifier = content,
                )
                is ManifestationPage.Read -> all.firstOrNull { it.id == current.id }?.let { manifestation ->
                    ManifestationReader(
                        manifestation = manifestation,
                        speechState = speechState,
                        onRead = { viewModel.onRead(manifestation) },
                        onStop = viewModel::onStop,
                        onEdit = { viewModel.onEdit(manifestation) },
                        onDelete = { viewModel.onDelete(manifestation.id) },
                        modifier = content,
                    )
                }
                is ManifestationPage.Edit -> ManifestationEditor(
                    draft = current.draft,
                    onChange = viewModel::onDraftChange,
                    onSave = viewModel::onSave,
                    onCancel = viewModel::onBack,
                    modifier = content,
                )
            }
        }
        if (page == ManifestationPage.List) {
            AddButton(
                label = stringResource(R.string.manifestation_add),
                onClick = viewModel::onAdd,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = dimens.screenPadding, bottom = dimens.spaceLg),
            )
        }
    }

    if (choosingVoice) {
        VoiceDialog(
            voices = voices,
            selected = voiceName,
            onSelect = viewModel::onSelectVoice,
            onPreview = { viewModel.onPreviewVoice(it, sample) },
            onDismiss = {
                viewModel.onStop()
                choosingVoice = false
            },
        )
    }
}
