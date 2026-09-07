package com.astro.app.core.astronomy

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/** Position du Soleil : ce dont on a besoin pour en déduire les horaires. */
data class SunState(
    /** Déclinaison, en degrés. */
    val declination: Double,
    /** Équation du temps, en minutes. Écart entre midi solaire vrai et midi moyen. */
    val equationOfTime: Double,
    /** Longitude écliptique apparente, en degrés. Sert à la phase de la Lune. */
    val apparentLongitude: Double,
)

/**
 * Position du Soleil, d'après les séries de l'algorithme NOAA.
 *
 * Précision de l'ordre de la minute d'arc sur la déclinaison pour les dates courantes,
 * largement suffisante pour des heures de lever et de coucher.
 *
 * Objet pur : aucune dépendance à Android, aucune donnée téléchargée. Les horaires ne
 * sont pas récupérés auprès d'un service, ils sont calculés — ce n'est pas de la donnée
 * inventée, c'est un calcul dont l'algorithme est publié et vérifiable.
 */
object SunPosition {

    fun at(julianDay: Double): SunState {
        val t = JulianDay.centuriesSinceJ2000(julianDay)

        val meanLongitude = wrap360(280.46646 + t * (36_000.76983 + t * 0.000_303_2))
        val meanAnomaly = 357.52911 + t * (35_999.05029 - 0.000_153_7 * t)
        val eccentricity = 0.016_708_634 - t * (0.000_042_037 + 0.000_000_126_7 * t)

        val centre = sinDeg(meanAnomaly) * (1.914_602 - t * (0.004_817 + 0.000_014 * t)) +
            sinDeg(2 * meanAnomaly) * (0.019_993 - 0.000_101 * t) +
            sinDeg(3 * meanAnomaly) * 0.000_289

        val trueLongitude = meanLongitude + centre
        val apparentLongitude = trueLongitude - 0.005_69 - 0.004_78 * sinDeg(125.04 - 1_934.136 * t)

        val meanObliquity = 23.0 + (26.0 + (21.448 - t * (46.815 + t * (0.000_59 - t * 0.001_813))) / 60.0) / 60.0
        val obliquity = meanObliquity + 0.002_56 * cosDeg(125.04 - 1_934.136 * t)

        val declination = Math.toDegrees(asin(sinDeg(obliquity) * sinDeg(apparentLongitude)))

        val y = tan(Math.toRadians(obliquity / 2.0)).let { it * it }
        val equationOfTime = 4.0 * Math.toDegrees(
            y * sinDeg(2 * meanLongitude) -
                2 * eccentricity * sinDeg(meanAnomaly) +
                4 * eccentricity * y * sinDeg(meanAnomaly) * cosDeg(2 * meanLongitude) -
                0.5 * y * y * sinDeg(4 * meanLongitude) -
                1.25 * eccentricity * eccentricity * sinDeg(2 * meanAnomaly),
        )
        return SunState(
            declination = declination,
            equationOfTime = equationOfTime,
            apparentLongitude = wrap360(apparentLongitude),
        )
    }
}

internal fun sinDeg(degrees: Double): Double = sin(Math.toRadians(degrees))
internal fun cosDeg(degrees: Double): Double = cos(Math.toRadians(degrees))
internal fun wrap360(degrees: Double): Double = ((degrees % 360.0) + 360.0) % 360.0
