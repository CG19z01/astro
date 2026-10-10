package com.astro.app.navigation

import androidx.navigation.NavController
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureId
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Nom de l'argument de route portant la date d'un planning. */
const val PLANNING_DATE_ARG = "date"

/**
 * Navigue vers une fonctionnalité depuis n'importe quel écran.
 *
 * Le `popUpTo` sur la Home garantit l'exigence produit : la pile ne contient jamais
 * plus que « Home + écran courant », donc le bouton ou geste Retour d'Android ramène
 * toujours sur la Home. Depuis la Home, Retour quitte l'application.
 *
 * Effet de bord voulu : quitter un écran détruit son entrée de navigation, donc son
 * état. C'est ce qui fait repartir le Calendrier de sa vue d'ouverture à chaque retour.
 *
 * Seule exception : [navigateToPlanningAbove].
 */
fun NavController.navigateToFeature(route: String) {
    if (route == FeatureCatalog.home.route) {
        popBackStack(FeatureCatalog.home.route, inclusive = false)
        return
    }
    navigate(route) {
        popUpTo(FeatureCatalog.home.route) { inclusive = false }
        launchSingleTop = true
    }
}

/**
 * Ouvre le planning d'une date précise.
 *
 * La date est sérialisée en ISO 8601 : format stable, indépendant de la langue de
 * l'appareil, et sûr dans une URL de navigation.
 */
fun NavController.navigateToPlanning(date: LocalDate) {
    navigateToFeature(planningRoute(date))
}

/**
 * Ouvre le planning d'une date PAR-DESSUS l'écran courant, au lieu de le remplacer.
 *
 * Employé par le Calendrier : on y choisit un jour, on écrit sa note, et Retour doit
 * ramener au Calendrier tel qu'on l'a laissé — même vue, même jour sélectionné — pour
 * y voir apparaître la bulle. La pile vaut alors « Home, Calendrier, Planning », et un
 * second Retour ramène sur la Home comme ailleurs.
 */
fun NavController.navigateToPlanningAbove(date: LocalDate) {
    navigate(planningRoute(date)) { launchSingleTop = true }
}

private fun planningRoute(date: LocalDate): String {
    val pattern = FeatureCatalog.definitionOf(FeatureId.PLANNING).route
    val encoded = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    return pattern.replace("{$PLANNING_DATE_ARG}", encoded)
}
