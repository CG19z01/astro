package com.astro.app.core.astronomy

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.asin

/**
 * Horaires et phase de la Lune pour un jour et un lieu.
 *
 * [moonrise] ou [moonset] peuvent être nuls, et **ce n'est pas une anomalie** : la Lune
 * se lève environ cinquante minutes plus tard chaque jour, si bien qu'environ un jour
 * sur vingt-cinq elle ne se lève pas, ou ne se couche pas, dans la journée civile.
 */
/** Phase de la Lune : fraction éclairée et sens d'évolution. */
data class MoonPhase(val illumination: Double, val waxing: Boolean)

data class MoonEvents(
    val moonrise: Instant?,
    val moonset: Instant?,
    /** Fraction éclairée du disque, dans [0, 1]. */
    val illumination: Double,
    val waxing: Boolean,
)

/**
 * Recherche des horaires de la Lune par balayage de sa hauteur.
 *
 * Il n'existe pas de formule directe : on évalue la hauteur d'heure en heure sur la
 * journée locale, on repère les changements de signe autour de l'horizon, puis on
 * affine par dichotomie.
 *
 * La hauteur de référence est +0,125° et non 0° : la parallaxe lunaire relève l'astre,
 * la réfraction atmosphérique aussi, et c'est le bord supérieur du disque qui compte.
 * Sans cette correction, les horaires seraient décalés de plusieurs minutes.
 *
 * Le balayage porte sur la journée **locale**, définie par le fuseau : c'est là que le
 * fuseau intervient réellement, au-delà du simple affichage.
 */
object MoonCalculator {

    private const val HORIZON_DEGREES = 0.125
    private const val REFINEMENT_STEPS = 12

    fun eventsFor(date: LocalDate, zone: ZoneId, latitude: Double, longitude: Double): MoonEvents {
        val start = date.atStartOfDay(zone).toInstant()
        val hours = (0..24).map { start.plusSeconds(it * 3_600L) }
        val altitudes = hours.map { altitude(it, latitude, longitude) }

        var rise: Instant? = null
        var set: Instant? = null

        for (index in 0 until hours.size - 1) {
            val before = altitudes[index]
            val after = altitudes[index + 1]
            if (before < 0 && after >= 0 && rise == null) {
                rise = refine(hours[index], hours[index + 1], latitude, longitude, rising = true)
            }
            if (before >= 0 && after < 0 && set == null) {
                set = refine(hours[index], hours[index + 1], latitude, longitude, rising = false)
            }
        }

        val state = MoonPosition.at(JulianDay.of(start.plusSeconds(43_200L)))
        return MoonEvents(
            moonrise = rise,
            moonset = set,
            illumination = state.illumination,
            waxing = state.waxing,
        )
    }

    /**
     * Phase de la Lune un jour donné.
     *
     * **Ne demande aucune coordonnée** : la phase est géocentrique, elle est la même
     * partout sur Terre. Seuls les horaires de lever et de coucher dépendent du lieu.
     * Un calendrier lunaire est donc affichable même sans position renseignée.
     */
    fun phaseOn(date: LocalDate, zone: ZoneId): MoonPhase {
        val noon = date.atStartOfDay(zone).toInstant().plusSeconds(43_200L)
        val state = MoonPosition.at(JulianDay.of(noon))
        return MoonPhase(illumination = state.illumination, waxing = state.waxing)
    }

    /** Hauteur de la Lune au-dessus de l'horizon de référence, en degrés. */
    private fun altitude(instant: Instant, latitude: Double, longitude: Double): Double {
        val julianDay = JulianDay.of(instant)
        val moon = MoonPosition.at(julianDay)
        val hourAngle = localSiderealTime(julianDay, longitude) - moon.rightAscension
        val sinAltitude = sinDeg(latitude) * sinDeg(moon.declination) +
            cosDeg(latitude) * cosDeg(moon.declination) * cosDeg(hourAngle)
        return Math.toDegrees(asin(sinAltitude.coerceIn(-1.0, 1.0))) - HORIZON_DEGREES
    }

    /** Dichotomie entre deux instants encadrant le passage de l'horizon. */
    private fun refine(
        from: Instant,
        to: Instant,
        latitude: Double,
        longitude: Double,
        rising: Boolean,
    ): Instant {
        var low = from
        var high = to
        repeat(REFINEMENT_STEPS) {
            val middle = Instant.ofEpochSecond((low.epochSecond + high.epochSecond) / 2)
            val crossed = if (rising) altitude(middle, latitude, longitude) < 0 else altitude(middle, latitude, longitude) >= 0
            if (crossed) low = middle else high = middle
        }
        return high
    }

    /** Temps sidéral local, en degrés. */
    private fun localSiderealTime(julianDay: Double, longitude: Double): Double =
        wrap360(280.16 + 360.985_623_5 * (julianDay - JulianDay.J2000) + longitude)
}
