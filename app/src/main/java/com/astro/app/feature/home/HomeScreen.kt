package com.astro.app.feature.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.FeatureId
import com.astro.app.core.config.HomeLayoutConfig
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.CelestialBackground
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.designsystem.components.onHorizontalSwipe
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.feature.home.components.DayBeadLayer
import com.astro.app.feature.home.components.HomeShortcuts
import java.time.LocalDate

/**
 * Page d'accueil.
 *
 * Trois couches : le fond céleste, les billes-journées, puis les points d'entrée.
 * Un swipe horizontal déplace le curseur temporel, ce qui met les deux billes à jour.
 * Un maintien long sur le dodécaèdre ramène ce curseur sur la journée en cours.
 */
@Composable
fun HomeScreen(
    states: Map<FeatureId, Boolean>,
    onOpenFeature: (String) -> Unit,
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val cursor by viewModel.cursor.collectAsState()
    val planningPreview by viewModel.planningPreview.collectAsState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .onHorizontalSwipe(key = cursor.scale, onSwipe = viewModel::onSwipe),
    ) {
        CelestialBackground()
        DayBeadLayer(
            width = maxWidth,
            height = maxHeight,
            cursor = cursor,
            locale = locale,
            planningPreview = planningPreview,
            onOpenPlanning = onOpenPlanning,
            modifier = Modifier.statusBarsPadding(),
        )
        HomeShortcuts(
            width = maxWidth,
            height = maxHeight,
            states = states,
            onOpenFeature = onOpenFeature,
            onResetToToday = viewModel::resetToToday,
            modifier = Modifier.statusBarsPadding(),
        )
        WakeTimeBlock(height = maxHeight, modifier = Modifier.statusBarsPadding())
    }
}

@Composable
private fun WakeTimeBlock(height: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Column(
        modifier = modifier
            .padding(horizontal = dimens.screenPadding)
            .offset(y = height * HomeLayoutConfig.WAKE_TIME_Y)
            .screenReveal(order = 4),
    ) {
        SectionLabel(text = stringResource(R.string.home_wake_time_label))
        Text(
            text = stringResource(R.string.home_value_unset),
            style = MaterialTheme.typography.titleLarge,
            color = palette.inkFaint,
            modifier = Modifier.padding(top = dimens.spaceXs),
        )
    }
}
