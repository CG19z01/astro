package com.astro.app.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.config.FeatureId
import com.astro.app.feature.astrology.AstrologyScreen
import com.astro.app.feature.calendar.CalendarScreen
import com.astro.app.feature.home.HomeScreen
import com.astro.app.feature.manifestation.ManifestationScreen
import com.astro.app.feature.phrases.PhrasesScreen
import com.astro.app.feature.planning.PlanningScreen
import com.astro.app.feature.settings.SettingsScreen

/**
 * Graphe de navigation, dérivé du catalogue de fonctionnalités.
 *
 * Le passage d'un écran à l'autre se fait en **fondu**, dans la même durée que la
 * bascule d'ambiance : la page et ses couleurs changent donc d'un seul mouvement au
 * lieu de se succéder. Le fondu s'applique à toutes les destinations — n'en animer
 * qu'une partie rendrait la navigation incohérente.
 *
 * L'état d'activation est passé sous forme de [State] et non de `Map` : `NavHost`
 * mémorise son graphe et ne reconstruit pas le bloc de construction quand les valeurs
 * capturées changent. Capturer une `Map` figerait l'état à sa première valeur, et une
 * fonctionnalité réactivée resterait inaccessible.
 */
@Composable
fun AstroNavHost(
    navController: NavHostController,
    states: State<Map<FeatureId, Boolean>>,
    modifier: Modifier = Modifier,
) {
    val returnHome = { navController.navigateToFeature(FeatureCatalog.home.route) }

    NavHost(
        navController = navController,
        startDestination = FeatureCatalog.home.route,
        modifier = modifier,
        enterTransition = { pageFadeIn() },
        exitTransition = { pageFadeOut() },
        popEnterTransition = { pageFadeIn() },
        popExitTransition = { pageFadeOut() },
    ) {
        composable(FeatureCatalog.home.route) {
            HomeScreen(
                states = states.value,
                onOpenFeature = navController::navigateToFeature,
                onOpenPlanning = navController::navigateToPlanning,
            )
        }
        feature(FeatureId.ASTROLOGY, states, returnHome) { AstrologyScreen() }
        feature(FeatureId.PHRASES, states, returnHome) { PhrasesScreen() }
        feature(FeatureId.CALENDAR, states, returnHome) { CalendarScreen() }
        feature(FeatureId.SETTINGS, states, returnHome) { SettingsScreen() }
        feature(FeatureId.MANIFESTATION, states, returnHome) { ManifestationScreen() }

        composable(
            route = FeatureCatalog.definitionOf(FeatureId.PLANNING).route,
            arguments = listOf(navArgument(PLANNING_DATE_ARG) { type = NavType.StringType }),
        ) {
            PlanningScreen()
        }
    }
}

/** Déclare une destination du catalogue, protégée par son état d'activation. */
private fun NavGraphBuilder.feature(
    id: FeatureId,
    states: State<Map<FeatureId, Boolean>>,
    onBlocked: () -> Unit,
    content: @Composable () -> Unit,
) {
    composable(FeatureCatalog.definitionOf(id).route) {
        if (states.value[id] == true) {
            content()
        } else {
            LaunchedEffect(id) { onBlocked() }
        }
    }
}

/** Apparition de la page entrante. */
private fun pageFadeIn(): EnterTransition =
    fadeIn(animationSpec = tween(AstroMotion.TRANSITION_MILLIS, easing = AstroMotion.Emphasized))

/** Effacement de la page sortante. */
private fun pageFadeOut(): ExitTransition =
    fadeOut(animationSpec = tween(AstroMotion.TRANSITION_MILLIS, easing = AstroMotion.Emphasized))
