package com.astro.app.core.settings

import com.astro.app.core.config.FeatureId
import kotlinx.coroutines.flow.Flow

/**
 * Accès aux préférences utilisateur.
 *
 * Interface volontairement indépendante du moyen de stockage. L'implémentation
 * actuelle est locale (DataStore). Un passage ultérieur à une synchronisation
 * distante se fera par une nouvelle implémentation, sans modifier l'interface
 * ni les écrans qui la consomment.
 */
interface SettingsRepository {

    /** État activé/désactivé de chaque fonctionnalité, défauts du catalogue déjà appliqués. */
    val featureStates: Flow<Map<FeatureId, Boolean>>

    /**
     * Active ou désactive une fonctionnalité.
     * Sans effet sur une fonctionnalité déclarée non désactivable dans le catalogue.
     */
    suspend fun setFeatureEnabled(id: FeatureId, enabled: Boolean)
}
