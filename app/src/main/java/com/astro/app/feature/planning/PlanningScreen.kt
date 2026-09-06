package com.astro.app.feature.planning

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

/** Délai avant enregistrement automatique, en millisecondes. */
private const val AUTOSAVE_DELAY_MILLIS = 600L

/**
 * Planning d'une journée, ouvert en appuyant sur une bille-journée.
 *
 * Le titre et la date proviennent de l'argument de navigation, donc de la bille : rien
 * n'est écrit en dur. La saisie est enregistrée automatiquement après une courte pause
 * de frappe, ce qui évite un bouton d'enregistrement et une écriture à chaque touche.
 */
@OptIn(FlowPreview::class)
@Composable
fun PlanningScreen(
    modifier: Modifier = Modifier,
    viewModel: PlanningViewModel = viewModel(factory = PlanningViewModel.Factory),
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val cursor = remember(viewModel.date) { TimeCursor.of(TimeScale.DAY, viewModel.date) }
    val stored by viewModel.text.collectAsState()

    var draft by rememberSaveable { mutableStateOf<String?>(null) }
    // Tant que l'utilisateur n'a rien saisi, l'écran suit ce qui est enregistré.
    val value = draft ?: stored

    LaunchedEffect(viewModel.date) {
        snapshotFlow { draft }
            .debounce(AUTOSAVE_DELAY_MILLIS)
            .collect { pending -> if (pending != null) viewModel.save(pending) }
    }

    AstroScreen(
        overline = PeriodLabels.title(cursor, locale),
        title = PeriodLabels.subtitle(cursor, locale),
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.planning_hint),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
            modifier = Modifier.screenReveal(order = 2),
        )
        OutlinedTextField(
            value = value,
            onValueChange = { draft = it },
            placeholder = { Text(stringResource(R.string.planning_placeholder)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .padding(top = dimens.spaceMd)
                .screenReveal(order = 3),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.accentSolar,
                unfocusedBorderColor = palette.surfaceBorder,
                focusedTextColor = palette.ink,
                unfocusedTextColor = palette.ink,
                cursorColor = palette.accentSolar,
            ),
        )
    }
}
