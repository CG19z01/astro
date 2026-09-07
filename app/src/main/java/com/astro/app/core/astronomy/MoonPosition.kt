package com.astro.app.core.astronomy

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos

/** Position de la Lune dans le repère équatorial, plus sa phase. */
data class MoonState(
    /** Ascension droite, en degrés. */
    val rightAscension: Double,
    /** Déclinaison, en degrés. */
    val declination: Double,
    /** Élongation par rapport au Soleil, en degrés, dans [0, 360[. */
    val elongation: Double,
) {
    /** Fraction éclairée du disque, dans [0, 1]. */
    val illumination: Double get() = (1.0 - cos(Math.toRadians(elongation))) / 2.0

    /** La Lune croît tant que son élongation n'a pas dépassé l'opposition. */
    val waxing: Boolean get() = elongation < 180.0
}

/**
 * Position de la Lune, série tronquée d'après Meeus.
 *
 * Neuf termes en longitude et quatre en latitude : précision de l'ordre de 0,2° sur la
 * position, soit une incertitude de quelques minutes sur les heures de lever et de
 * coucher. C'est suffisant pour un affichage courant, et cette limite est assumée
 * plutôt que masquée.
 *
 * Objet pur, vérifié par test à partir de propriétés physiques.
 */
object MoonPosition {

    fun at(julianDay: Double): MoonState {
        val d = julianDay - JulianDay.J2000

        val meanLongitude = 218.3165 + 13.176_396_48 * d
        val elongationAngle = 297.8502 + 12.190_749_12 * d
        val sunAnomaly = 357.5291 + 0.985_600_28 * d
        val moonAnomaly = 134.9634 + 13.064_992_95 * d
        val latitudeArgument = 93.2721 + 13.229_350_24 * d

        val longitude = meanLongitude +
            6.289 * sinDeg(moonAnomaly) +
            1.274 * sinDeg(2 * elongationAngle - moonAnomaly) +
            0.658 * sinDeg(2 * elongationAngle) +
            0.214 * sinDeg(2 * moonAnomaly) -
            0.186 * sinDeg(sunAnomaly) -
            0.114 * sinDeg(2 * latitudeArgument) +
            0.059 * sinDeg(2 * elongationAngle - 2 * moonAnomaly) +
            0.057 * sinDeg(2 * elongationAngle - sunAnomaly - moonAnomaly)

        val latitude = 5.128 * sinDeg(latitudeArgument) +
            0.281 * sinDeg(moonAnomaly + latitudeArgument) -
            0.278 * sinDeg(latitudeArgument - moonAnomaly) -
            0.173 * sinDeg(2 * elongationAngle - latitudeArgument)

        val obliquity = 23.4393 - 3.563e-7 * d
        val sinLat = sinDeg(latitude)
        val cosLat = cosDeg(latitude)

        val rightAscension = Math.toDegrees(
            atan2(
                sinDeg(longitude) * cosDeg(obliquity) - (sinLat / cosLat) * sinDeg(obliquity),
                cosDeg(longitude),
            ),
        )
        val declination = Math.toDegrees(
            asin(sinLat * cosDeg(obliquity) + cosLat * sinDeg(obliquity) * sinDeg(longitude)),
        )

        val sunLongitude = SunPosition.at(julianDay).apparentLongitude
        return MoonState(
            rightAscension = wrap360(rightAscension),
            declination = declination,
            elongation = wrap360(longitude - sunLongitude),
        )
    }
}
