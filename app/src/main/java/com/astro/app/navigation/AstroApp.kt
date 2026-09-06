package com.astro.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.astro.app.core.config.Ambiance
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.LocalAstroPalette
import com.astro.app.core.di.AppContainer
import com.astro.app.core.settings.FeatureStates

/**
 * Racine de l'interface.
 *
 * L'ambiance visuelle est déduite de la destination courante via le catalogue :
 * naviguer vers l'Astrologie fait basculer toute l'application vers l'aube, barre
 * de navigation comprise, par un fondu et non par une coupure.
 */
@Composable
fun AstroApp(container: AppContainer, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val states = container.settingsRepository.featureStates
        .collectAsState(initial = FeatureStates.defaults)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val ambiance = FeatureCatalog.definitionOfRouteOrNull(currentRoute)?.ambiance
        ?: FeatureCatalog.home.ambiance

    AstroTheme(ambiance = ambiance) {
        SyncSystemBars(ambiance)
        // Socle opaque : le fond céleste de l'écran s'arrête au-dessus de la barre de
        // navigation, et sans ce socle le fond de fenêtre du système transparaîtrait.
        Box(modifier = Modifier.fillMaxSize().background(LocalAstroPalette.current.skyBottom)) {
        Scaffold(
            modifier = modifier,
            containerColor = Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
            bottomBar = {
                AstroBottomBar(
                    states = states.value,
                    currentRoute = currentRoute,
                    onSelect = navController::navigateToFeature,
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                AstroNavHost(
                    navController = navController,
                    states = states,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }
        }
        }
    }
}

/** Les icônes de la barre système doivent rester lisibles dans les deux ambiances. */
@Composable
private fun SyncSystemBars(ambiance: Ambiance) {
    val view = LocalView.current
    DisposableEffect(ambiance) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            val lightIcons = ambiance == Ambiance.LIGHT
            controller.isAppearanceLightStatusBars = lightIcons
            controller.isAppearanceLightNavigationBars = lightIcons
        }
        onDispose { }
    }
}
