package com.astro.app.core.speech

import kotlinx.coroutines.flow.StateFlow

/** État du moteur de lecture. */
enum class SpeechState {
    /** Le moteur démarre : rien n'est encore possible. */
    STARTING,
    READY,
    SPEAKING,
    /** Pas de moteur de synthèse, ou pas de français : la lecture est impossible. */
    UNAVAILABLE,
}

/**
 * Une voix installée sur le téléphone.
 *
 * **Le genre n'y figure pas** : Android ne l'expose pas de façon fiable (limitation
 * connue, voir CLAUDE.md). L'utilisateur choisit à l'oreille, avec l'aperçu.
 */
data class SpeechVoice(
    val name: String,
    /** Synthèse faite en ligne : meilleure qualité possible, mais exige le réseau. */
    val needsNetwork: Boolean,
)
// « Non installée » (KEY_FEATURE_NOT_INSTALLED) n'est volontairement pas exposé : le
// moteur Google le signale pour des voix qui parlent pourtant (constaté le 2026-10-11).

/** Lecture à voix haute. Interface : testable sans Android. */
interface SpeechService {
    val state: StateFlow<SpeechState>

    /** Voix françaises disponibles ; vide tant que le moteur démarre. */
    val voices: StateFlow<List<SpeechVoice>>

    /** Démarre le moteur s'il ne l'est pas. Sans effet ensuite. */
    fun prepare()

    /** Lit [text] avec la voix [voiceName], ou la voix française par défaut si nulle. */
    fun speak(text: String, voiceName: String?)

    fun stop()
}
