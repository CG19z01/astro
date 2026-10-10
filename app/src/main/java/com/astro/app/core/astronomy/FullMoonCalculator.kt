package com.astro.app.core.astronomy

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.floor

/**
 * Instants de pleine lune, et jour civil local où chacun tombe.
 *
 * **Pourquoi pas le calcul de phase existant.** [MoonCalculator.phaseOn] donne la
 * fraction éclairée à midi : il ne dit pas QUAND la Lune est pleine, et une Lune
 * éclairée à 99 % la veille n'est pas une pleine lune. Chercher l'instant où
 * l'élongation de [MoonPosition] vaut 180° a été mesuré : jusqu'à 16,5 min d'écart
 * avec l'USNO sur 2024-2027, assez pour changer le jour d'une pleine lune proche de
 * minuit. On emploie donc l'algorithme dédié de Meeus (chapitre 49), plus précis.
 *
 * **Précision constatée.** Sur les 49 pleines lunes de 2024 à 2027 publiées par l'U.S.
 * Naval Observatory, écart maximal de 0,74 min, moyen de 0,14 min ; l'USNO arrondissant
 * à la minute, c'est la limite de ce que la comparaison peut montrer. Hors de cette
 * période, la précision n'a pas été vérifiée.
 *
 * **Limite assumée.** Une pleine lune à moins d'une minute de minuit peut être rangée
 * sur le jour voisin. Cela ne concerne qu'une infime fraction des pleines lunes.
 *
 * Objet pur, sans dépendance à Android, couvert par des tests unitaires.
 */
object FullMoonCalculator {

    /** Lunaison moyenne, en jours. */
    private const val SYNODIC_MONTH = 29.530588861

    /** Jour julien de la nouvelle lune moyenne de référence (k = 0, janvier 2000). */
    private const val EPOCH = 2_451_550.09766

    /** Lunaisons par siècle julien : convertit k en siècles depuis J2000. */
    private const val LUNATIONS_PER_CENTURY = 1_236.85

    /**
     * Écart entre le temps dynamique de l'algorithme et le temps universel, en secondes.
     * Valeur proche de celle des années 2020 ; l'ignorer décalerait chaque instant
     * d'environ une minute. Elle varie lentement et devra être revue à long terme.
     */
    private const val DELTA_T_SECONDS = 69L

    /**
     * Pleines lunes dont le jour civil local tombe entre [from] et [to] inclus.
     * Une pleine lune par date au plus : deux sont séparées de plus de 29 jours.
     */
    fun between(from: LocalDate, to: LocalDate, zone: ZoneId): Map<LocalDate, Instant> {
        val start = from.atStartOfDay(zone).toInstant()
        val end = to.plusDays(1).atStartOfDay(zone).toInstant()
        val result = linkedMapOf<LocalDate, Instant>()

        // On part d'une lunaison avant le début, l'instant corrigé pouvant précéder
        // l'instant moyen de plus d'une demi-journée.
        var k = floor((JulianDay.of(start) - EPOCH) / SYNODIC_MONTH) - 1 + 0.5
        while (true) {
            val instant = instantOf(k)
            if (instant >= end) break
            if (instant >= start) result[instant.atZone(zone).toLocalDate()] = instant
            k += 1.0
        }
        return result
    }

    /** Instant de la pleine lune de rang [k] (demi-entier : 0,5 est celle de janvier 2000). */
    fun instantOf(k: Double): Instant {
        val t = k / LUNATIONS_PER_CENTURY
        val meanJde = EPOCH + SYNODIC_MONTH * k +
            0.00015437 * t * t - 0.000000150 * t * t * t + 0.00000000073 * t * t * t * t

        val e = 1 - 0.002516 * t - 0.0000074 * t * t
        val m = 2.5534 + 29.10535670 * k - 0.0000014 * t * t - 0.00000011 * t * t * t
        val mp = 201.5643 + 385.81693528 * k + 0.0107582 * t * t + 0.00001238 * t * t * t -
            0.000000058 * t * t * t * t
        val f = 160.7108 + 390.67050284 * k - 0.0016118 * t * t - 0.00000227 * t * t * t +
            0.000000011 * t * t * t * t
        val omega = 124.7746 - 1.56375588 * k + 0.0020672 * t * t + 0.00000215 * t * t * t

        val jde = meanJde +
            LunarPhaseTerms.fullMoonCorrection(m, mp, f, omega, e) +
            LunarPhaseTerms.planetaryCorrection(k, t)
        return JulianDay.toInstant(jde).minusSeconds(DELTA_T_SECONDS)
    }
}
