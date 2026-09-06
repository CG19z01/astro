package com.astro.app.core.config

import java.util.Locale

/**
 * Langues réellement fournies par l'application.
 *
 * Sert à formater les dates dans la même langue que les textes affichés. Sans cela,
 * un appareil configuré en anglais afficherait une interface en français avec des
 * noms de jours en anglais.
 *
 * Ajouter une traduction : créer `res/values-<langue>/strings.xml` ET ajouter la
 * langue à [SUPPORTED]. Les deux vont ensemble.
 */
object LocaleConfig {

    /** Langue des ressources par défaut, c'est-à-dire de `res/values/strings.xml`. */
    val DEFAULT: Locale = Locale.FRENCH

    /** Langues pour lesquelles des textes existent réellement. */
    val SUPPORTED: List<Locale> = listOf(Locale.FRENCH)

    /**
     * Langue de formatage à utiliser pour un appareil donné : celle de l'appareil si
     * l'application est traduite dans cette langue, sinon celle des ressources par défaut.
     */
    fun resolve(deviceLocale: Locale): Locale =
        SUPPORTED.firstOrNull { it.language == deviceLocale.language } ?: DEFAULT
}
