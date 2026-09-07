package com.astro.app.feature.settings

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Mise en forme des horaires d'éphémérides.
 *
 * C'est ici, et seulement ici, qu'intervient le fuseau horaire : les horaires sont
 * calculés à partir des coordonnées géographiques, le fuseau ne fait que décider de
 * l'heure locale à laquelle on les affiche.
 */
object EphemerisFormat {

    private const val PATTERN = "HH:mm"

    fun time(instant: Instant?, zone: ZoneId, locale: Locale, fallback: String): String =
        instant?.atZone(zone)
            ?.format(DateTimeFormatter.ofPattern(PATTERN, locale))
            ?: fallback

    /** Durée en minutes présentée sous la forme « 12 h 34 ». */
    fun duration(minutes: Long?, fallback: String): String =
        minutes?.let { "${it / 60} h ${(it % 60).toString().padStart(2, '0')}" } ?: fallback

    /** Fraction éclairée présentée en pourcentage entier. */
    fun percentage(fraction: Double): String = "${Math.round(fraction * 100)} %"
}
