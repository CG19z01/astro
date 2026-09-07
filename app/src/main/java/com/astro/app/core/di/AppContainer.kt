package com.astro.app.core.di

import com.astro.app.core.location.DeviceLocationProvider
import com.astro.app.core.location.SessionLocation
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.settings.SettingsRepository

/**
 * Conteneur de dépendances de l'application.
 *
 * Injection manuelle assumée : le projet n'a pas encore le volume qui justifierait
 * une bibliothèque comme Hilt. Les écrans dépendent des interfaces exposées ici,
 * donc remplacer une implémentation (locale → distante) ne touche qu'à ce fichier.
 */
class AppContainer(
    val settingsRepository: SettingsRepository,
    val planningRepository: PlanningRepository,
    val locationProvider: DeviceLocationProvider,
    /** Position de session : en mémoire uniquement, jamais enregistrée. */
    val sessionLocation: SessionLocation,
)
