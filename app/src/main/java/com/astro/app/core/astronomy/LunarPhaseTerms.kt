package com.astro.app.core.astronomy

/**
 * Termes correctifs de l'instant de pleine lune, d'après J. Meeus, *Astronomical
 * Algorithms*, 2e éd., chapitre 49 (« Phases of the Moon »), table 49.A et termes
 * planétaires A1 à A14.
 *
 * Seules les corrections de la PLEINE lune figurent ici : les autres phases ont leurs
 * propres coefficients, que le projet n'emploie pas.
 *
 * Arguments, en degrés : `m` anomalie moyenne du Soleil, `mp` anomalie moyenne de la
 * Lune, `f` argument de latitude, `omega` longitude du nœud ascendant, `e` facteur
 * d'excentricité de l'orbite terrestre. Résultat en jours.
 */
internal object LunarPhaseTerms {

    fun fullMoonCorrection(m: Double, mp: Double, f: Double, omega: Double, e: Double): Double =
        -0.40614 * sinDeg(mp) +
            0.17302 * e * sinDeg(m) +
            0.01614 * sinDeg(2 * mp) +
            0.01043 * sinDeg(2 * f) +
            0.00734 * e * sinDeg(mp - m) -
            0.00515 * e * sinDeg(mp + m) +
            0.00209 * e * e * sinDeg(2 * m) -
            0.00111 * sinDeg(mp - 2 * f) -
            0.00057 * sinDeg(mp + 2 * f) +
            0.00056 * e * sinDeg(2 * mp + m) -
            0.00042 * sinDeg(3 * mp) +
            0.00042 * e * sinDeg(m + 2 * f) +
            0.00038 * e * sinDeg(m - 2 * f) -
            0.00024 * e * sinDeg(2 * mp - m) -
            0.00017 * sinDeg(omega) -
            0.00007 * sinDeg(mp + 2 * m) +
            0.00004 * sinDeg(2 * mp - 2 * f) +
            0.00004 * sinDeg(3 * m) +
            0.00003 * sinDeg(mp + m - 2 * f) +
            0.00003 * sinDeg(2 * mp + 2 * f) -
            0.00003 * sinDeg(mp + m + 2 * f) +
            0.00003 * sinDeg(mp - m + 2 * f) -
            0.00002 * sinDeg(mp - m - 2 * f) -
            0.00002 * sinDeg(3 * mp + m) +
            0.00002 * sinDeg(4 * mp)

    /** Un terme planétaire : argument `a0 + a1·k`, amplitude en jours. */
    private class PlanetaryTerm(val a0: Double, val a1: Double, val amplitude: Double)

    private val planetary = listOf(
        PlanetaryTerm(299.77, 0.107408, 0.000325),
        PlanetaryTerm(251.88, 0.016321, 0.000165),
        PlanetaryTerm(251.83, 26.651886, 0.000164),
        PlanetaryTerm(349.42, 36.412478, 0.000126),
        PlanetaryTerm(84.66, 18.206239, 0.000110),
        PlanetaryTerm(141.74, 53.303771, 0.000062),
        PlanetaryTerm(207.14, 2.453732, 0.000060),
        PlanetaryTerm(154.84, 7.306860, 0.000056),
        PlanetaryTerm(34.52, 27.261239, 0.000047),
        PlanetaryTerm(207.19, 0.121824, 0.000042),
        PlanetaryTerm(291.34, 1.844379, 0.000040),
        PlanetaryTerm(161.72, 24.198154, 0.000037),
        PlanetaryTerm(239.56, 25.513099, 0.000035),
        PlanetaryTerm(331.55, 3.592518, 0.000023),
    )

    /** Seul le premier argument (A1) porte un terme en T². */
    private const val A1_T2 = -0.009173

    fun planetaryCorrection(k: Double, t: Double): Double =
        planetary.mapIndexed { index, term ->
            val argument = term.a0 + term.a1 * k + if (index == 0) A1_T2 * t * t else 0.0
            term.amplitude * sinDeg(argument)
        }.sum()
}
