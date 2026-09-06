package com.astro.app.core.settings

import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureDefinition
import com.astro.app.core.config.FeatureId

/**
 * Logique de résolution de l'état activé/désactivé des fonctionnalités.
 *
 * Volontairement sans dépendance au stockage ni à Android : c'est une fonction pure,
 * ce qui la rend testable en test unitaire JVM (voir `FeatureStatesTest`).
 */
object FeatureStates {

    /** État de référence, avant tout réglage de l'utilisateur. */
    val defaults: Map<FeatureId, Boolean> =
        FeatureCatalog.definitions.associate { it.id to it.defaultEnabled }

    /**
     * Combine les réglages persistés avec le catalogue.
     *
     * - une fonctionnalité absente de [stored] retombe sur sa valeur par défaut ;
     * - une fonctionnalité inscrite comme non désactivable est toujours activée,
     *   même si une valeur `false` traîne dans le stockage (migration, réglage obsolète) ;
     * - une clé de [stored] inconnue du catalogue est ignorée.
     */
    fun merge(stored: Map<FeatureId, Boolean>): Map<FeatureId, Boolean> =
        FeatureCatalog.definitions.associate { definition ->
            definition.id to resolve(definition, stored[definition.id])
        }

    private fun resolve(definition: FeatureDefinition, storedValue: Boolean?): Boolean =
        when {
            !definition.canBeDisabled -> true
            else -> storedValue ?: definition.defaultEnabled
        }

    /** Fonctionnalités à afficher dans la barre de navigation basse, dans l'ordre du catalogue. */
    fun visibleInBottomBar(states: Map<FeatureId, Boolean>): List<FeatureDefinition> =
        FeatureCatalog.bottomBarFeatures.filter { states[it.id] == true }

    /** `true` si la route est atteignable, c'est-à-dire connue ET activée. */
    fun isRouteReachable(states: Map<FeatureId, Boolean>, route: String?): Boolean {
        val definition = FeatureCatalog.definitionOfRouteOrNull(route) ?: return false
        return states[definition.id] == true
    }
}
