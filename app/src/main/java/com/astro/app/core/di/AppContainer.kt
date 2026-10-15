package com.astro.app.core.di

import com.astro.app.core.alarm.NextAlarmSource
import com.astro.app.core.location.DeviceLocationProvider
import com.astro.app.core.location.SessionLocation
import com.astro.app.core.manifestation.ManifestationRepository
import com.astro.app.core.phrases.DailyPhrase
import com.astro.app.core.phrases.PhraseRepository
import com.astro.app.core.speech.SpeechService
import kotlinx.coroutines.flow.map
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
    /** Prochain réveil de l'appareil, lu auprès d'Android. */
    val nextAlarmSource: NextAlarmSource,
    /** Phrases importées par l'utilisateur, et journal des phrases montrées. */
    val phraseRepository: PhraseRepository,
    val manifestationRepository: ManifestationRepository,
    /** Lecture à voix haute ; le moteur ne démarre qu'à la première demande. */
    val speechService: SpeechService,
) {
    /** Phrase du jour, choisie selon le genre et les thèmes réglés par l'utilisateur. */
    fun dailyPhrase(): DailyPhrase =
        DailyPhrase(phraseRepository, settingsRepository.preferences.map { it.phraseWishes })
}
