package com.astro.app.core.astronomy

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Conversion entre le temps civil et le jour julien, l'échelle utilisée par tous les
 * calculs astronomiques.
 *
 * Le jour julien 2451545,0 correspond au 1er janvier 2000 à 12h00 UTC : c'est l'époque
 * J2000, origine des séries employées ici.
 */
object JulianDay {

    const val J2000 = 2_451_545.0

    /** Nombre de jours dans un siècle julien. */
    const val DAYS_PER_CENTURY = 36_525.0

    private const val UNIX_EPOCH_JD = 2_440_587.5
    private const val SECONDS_PER_DAY = 86_400.0

    fun of(instant: Instant): Double = UNIX_EPOCH_JD + instant.epochSecond / SECONDS_PER_DAY

    /** Jour julien à minuit UTC pour cette date. */
    fun atMidnightUtc(date: LocalDate): Double = of(date.atStartOfDay(ZoneOffset.UTC).toInstant())

    fun toInstant(julianDay: Double): Instant =
        Instant.ofEpochMilli(((julianDay - UNIX_EPOCH_JD) * SECONDS_PER_DAY * 1_000.0).toLong())

    /** Siècles juliens écoulés depuis J2000. */
    fun centuriesSinceJ2000(julianDay: Double): Double = (julianDay - J2000) / DAYS_PER_CENTURY
}
