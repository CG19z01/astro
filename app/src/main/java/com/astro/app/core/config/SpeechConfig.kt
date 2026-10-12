package com.astro.app.core.config

/** Réglages de la lecture vocale des manifestations. */
object SpeechConfig {
    /** Langue de lecture : le français de France. */
    const val LANGUAGE_TAG: String = "fr-FR"

    /** Voix proposées : celles du français de France (pas du Canada, demandé « français »). */
    const val VOICE_LANGUAGE: String = "fr"
    const val VOICE_COUNTRY: String = "FR"
}
