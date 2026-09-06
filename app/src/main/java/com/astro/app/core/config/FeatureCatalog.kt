package com.astro.app.core.config

import com.astro.app.R

/**
 * Source unique de vérité des fonctionnalités de l'application.
 *
 * Ajouter une page se fait en deux gestes :
 *  1. ajouter une constante dans [FeatureId] et une entrée dans [definitions] ;
 *  2. déclarer son écran dans le graphe de navigation.
 *
 * La barre de navigation, la page Paramètres et le graphe de navigation sont tous
 * dérivés de cette liste : aucun autre fichier existant n'est à modifier.
 */
object FeatureCatalog {

    val definitions: List<FeatureDefinition> = listOf(
        FeatureDefinition(
            id = FeatureId.HOME,
            route = "home",
            titleRes = R.string.feature_home,
            defaultEnabled = true,
            // La Home est la destination de départ et la cible du bouton Retour :
            // la désactiver rendrait l'application inutilisable.
            canBeDisabled = false,
            inBottomBar = true,
            ambiance = Ambiance.DARK,
        ),
        FeatureDefinition(
            id = FeatureId.ASTROLOGY,
            route = "astrology",
            titleRes = R.string.feature_astrology,
            defaultEnabled = true,
            canBeDisabled = true,
            inBottomBar = true,
            ambiance = Ambiance.LIGHT,
        ),
        FeatureDefinition(
            id = FeatureId.PHRASES,
            route = "phrases",
            titleRes = R.string.feature_phrases,
            defaultEnabled = true,
            canBeDisabled = true,
            inBottomBar = true,
            ambiance = Ambiance.LIGHT,
        ),
        FeatureDefinition(
            id = FeatureId.CALENDAR,
            route = "calendar",
            titleRes = R.string.feature_calendar,
            defaultEnabled = true,
            canBeDisabled = true,
            inBottomBar = true,
            ambiance = Ambiance.DARK,
        ),
        FeatureDefinition(
            id = FeatureId.SETTINGS,
            route = "settings",
            titleRes = R.string.feature_settings,
            defaultEnabled = true,
            // Sans les Paramètres, l'utilisateur ne pourrait plus rien réactiver.
            canBeDisabled = false,
            inBottomBar = true,
            ambiance = Ambiance.DARK,
        ),
        FeatureDefinition(
            id = FeatureId.MANIFESTATION,
            route = "manifestation",
            titleRes = R.string.feature_manifestation,
            defaultEnabled = true,
            canBeDisabled = true,
            // Accessible par le dodécaèdre sur la Home, pas par la barre du bas.
            // C'est ce qui donne 5 boutons pour 6 pages, conformément à la maquette.
            inBottomBar = false,
            ambiance = Ambiance.LIGHT,
        ),
        FeatureDefinition(
            id = FeatureId.PLANNING,
            // Route paramétrée : la date vient de la bille sur laquelle on appuie.
            route = "planning/{date}",
            titleRes = R.string.feature_planning,
            defaultEnabled = true,
            // Sans le planning, une bille-journée ne mènerait nulle part.
            canBeDisabled = false,
            inBottomBar = false,
            ambiance = Ambiance.DARK,
        ),
    )

    val home: FeatureDefinition = definitionOf(FeatureId.HOME)

    val bottomBarFeatures: List<FeatureDefinition> = definitions.filter { it.inBottomBar }

    fun definitionOf(id: FeatureId): FeatureDefinition =
        definitions.firstOrNull { it.id == id }
            ?: error("Fonctionnalité $id absente du catalogue")

    fun definitionOfRouteOrNull(route: String?): FeatureDefinition? =
        definitions.firstOrNull { it.route == route }
}
