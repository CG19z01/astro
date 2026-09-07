package com.astro.app.core.astronomy

import java.time.Instant
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.tan

/**
 * Horaires du Soleil pour un jour et un lieu.
 *
 * [sunrise] et [sunset] sont nuls aux latitudes où l'astre ne se lève ni ne se couche
 * ce jour-là ; [polarDay] et [polarNight] disent alors laquelle des deux situations
 * s'applique. Renvoyer une heure arbitraire dans ce cas serait faux.
 */
data class SunEvents(
    val sunrise: Instant?,
    val solarNoon: Instant,
    val sunset: Instant?,
    val polarDay: Boolean,
    val polarNight: Boolean,
) {
    /** Durée du jour en minutes, ou null si l'astre ne se lève ni ne se couche. */
    val dayLengthMinutes: Long?
        get() = if (sunrise == null || sunset == null) null
        else (sunset.epochSecond - sunrise.epochSecond) / 60
}

/**
 * Calcul des horaires du Soleil selon l'algorithme NOAA.
 *
 * L'angle zénithal retenu pour le lever et le coucher est 90,833° : les 90° géométriques
 * corrigés de la réfraction atmosphérique et du demi-diamètre apparent du disque.
 * Sans cette correction, les horaires seraient décalés de plusieurs minutes.
 *
 * La longitude intervient dans le calcul ; le fuseau horaire, lui, ne sert qu'à
 * l'affichage. Deux lieux au même fuseau mais à des longitudes différentes n'ont pas
 * le même lever de soleil.
 */
object SunCalculator {

    private const val SUNRISE_ZENITH = 90.833
    private const val MINUTES_PER_DAY = 1_440.0

    fun eventsFor(date: LocalDate, latitude: Double, longitude: Double): SunEvents {
        val midnight = JulianDay.atMidnightUtc(date)
        // Position évaluée en milieu de journée : le Soleil bouge peu en 24 h, et
        // l'évaluer à minuit décalerait légèrement les deux horaires dans le même sens.
        val sun = SunPosition.at(midnight + 0.5)

        val noonMinutes = 720.0 - 4.0 * longitude - sun.equationOfTime
        val solarNoon = JulianDay.toInstant(midnight + noonMinutes / MINUTES_PER_DAY)

        val cosHourAngle = (cosDeg(SUNRISE_ZENITH) / (cosDeg(latitude) * cosDeg(sun.declination))) -
            tan(Math.toRadians(latitude)) * tan(Math.toRadians(sun.declination))

        if (abs(cosHourAngle) > 1.0) {
            // Pas de lever ni de coucher : le Soleil reste au-dessus ou au-dessous de
            // l'horizon toute la journée.
            return SunEvents(
                sunrise = null,
                solarNoon = solarNoon,
                sunset = null,
                polarDay = cosHourAngle < -1.0,
                polarNight = cosHourAngle > 1.0,
            )
        }

        val hourAngle = Math.toDegrees(acos(cosHourAngle))
        return SunEvents(
            sunrise = JulianDay.toInstant(midnight + (noonMinutes - 4.0 * hourAngle) / MINUTES_PER_DAY),
            solarNoon = solarNoon,
            sunset = JulianDay.toInstant(midnight + (noonMinutes + 4.0 * hourAngle) / MINUTES_PER_DAY),
            polarDay = false,
            polarNight = false,
        )
    }
}
