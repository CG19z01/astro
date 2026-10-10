package com.astro.app.feature.planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import com.astro.app.feature.planning.components.ManualOrderBar
import com.astro.app.feature.planning.components.PlanningLineRow
import com.astro.app.feature.planning.components.ReorderableLines
import com.astro.app.feature.planning.components.TimeWheelDialog
import java.time.LocalTime

/**
 * Planning d'une journée, en lignes : un texte d'une ligne et une heure facultative.
 *
 * Les lignes vivent dans le ViewModel ; chacune est rendue sous la clé de son
 * identifiant ([ReorderableLines], qui permet aussi de les déplacer par appui long), si bien qu'une ligne ajoutée ou retirée ne fait pas passer le curseur,
 * le texte ou l'heure d'une ligne à une autre.
 *
 * La liste défile et laisse la place au clavier (`imePadding`) : sur un petit écran, le
 * champ en cours de saisie reste visible. L'heure se choisit dans une fenêtre, qui ne
 * se fait pas masquer par le clavier.
 */
@Composable
fun PlanningScreen(
    modifier: Modifier = Modifier,
    viewModel: PlanningViewModel = viewModel(factory = PlanningViewModel.Factory),
) {
    val dimens = AstroTheme.dimens
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val cursor = remember(viewModel.date) { TimeCursor.of(TimeScale.DAY, viewModel.date) }
    val lines by viewModel.lines.collectAsState()
    val manualOrder by viewModel.manualOrder.collectAsState()
    // Ligne dont on choisit l'heure. Survit à la rotation, comme la fenêtre ouverte.
    var pickingFor by rememberSaveable { mutableStateOf<Long?>(null) }

    AstroScreen(
        overline = PeriodLabels.title(cursor, locale),
        title = PeriodLabels.subtitle(cursor, locale),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = dimens.spaceXxl)
                .screenReveal(order = 2),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceSm),
        ) {
            if (manualOrder) ManualOrderBar(onReset = viewModel::onResetOrder)
            val current = lines.orEmpty()
            ReorderableLines(lines = current, onMove = viewModel::onMoveLine) { line, handle ->
                PlanningLineRow(
                    line = line,
                    onTextChange = { viewModel.onTextChange(line.id, it) },
                    onPickTime = { pickingFor = line.id },
                    onLeave = { viewModel.onLineLeft(line.id) },
                    handle = handle,
                    onNudge = { delta -> viewModel.onMoveLine(line.id, current.indexOf(line) + delta) },
                )
            }
        }
    }

    val picked = lines?.firstOrNull { it.id == pickingFor }
    if (picked != null) {
        TimeWheelDialog(
            current = picked.time,
            suggestion = remember { suggestedTime() },
            onConfirm = { time ->
                viewModel.onTimeChange(picked.id, time)
                pickingFor = null
            },
            onRemove = {
                viewModel.onTimeChange(picked.id, null)
                pickingFor = null
            },
            onDismiss = { pickingFor = null },
        )
    }
}

/** Point de départ des roues pour une ligne sans heure : maintenant, au pas des minutes. */
private fun suggestedTime(): LocalTime {
    val now = LocalTime.now()
    return now.withMinute(now.minute / PlanningConfig.MINUTE_STEP * PlanningConfig.MINUTE_STEP)
        .withSecond(0).withNano(0)
}
