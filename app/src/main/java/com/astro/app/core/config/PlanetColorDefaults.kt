package com.astro.app.core.config

import java.time.DayOfWeek

/**
 * Valeurs par défaut et palette proposée pour la couleur des planètes-jours.
 *
 * Couleurs en ARGB entier : c'est la forme stockée, indépendante de Compose, donc
 * manipulable par la couche de données et testable sans Android.
 *
 * Centralisées ici et nulle part ailleurs : changer la palette proposée ou les valeurs
 * initiales se fait dans ce seul fichier.
 */
object PlanetColorDefaults {

    /** Couleur des jours de semaine. */
    const val WEEKDAY = 0xFF9DAEFF.toInt()

    /** Couleur du week-end. */
    const val WEEKEND = 0xFFE8C48A.toInt()

    /** Couleur employée par une règle de mot-clé nouvellement créée. */
    const val KEYWORD = 0xFFB794F6.toInt()

    /**
     * Palette proposée dans les Paramètres.
     *
     * Choisie dans l'univers astral de l'application : pervenche lunaire, or solaire,
     * violet cosmique, et quelques teintes voisines. L'utilisateur choisit parmi
     * celles-ci ; la saisie d'une couleur libre n'est pas offerte à ce stade.
     */
    val PALETTE: List<Int> = listOf(
        0xFF9DAEFF.toInt(),
        0xFF7E8FE8.toInt(),
        0xFFB794F6.toInt(),
        0xFFD98FC4.toInt(),
        0xFFE8C48A.toInt(),
        0xFFE0996A.toInt(),
        0xFF8FD3C4.toInt(),
        0xFF6FB3D9.toInt(),
    )

    /** Une couleur initiale par jour, prise dans la palette pour rester cohérente. */
    val PER_DAY: Map<DayOfWeek, Int> = DayOfWeek.entries.associateWith { day ->
        PALETTE[(day.ordinal) % PALETTE.size]
    }
}
