package com.astro.app.feature.home.components

import kotlin.math.sqrt
import kotlin.random.Random

/** Un cratère, décrit en fractions du rayon de l'astre. */
data class Crater(val x: Float, val y: Float, val radius: Float, val depth: Float)

/**
 * Champ de cratères de la Lune.
 *
 * Tiré une fois avec une graine fixe : la Lune doit être la même à chaque affichage.
 * Les cratères sont placés dans le disque unité par tirage rejeté, et non par un tirage
 * en coordonnées polaires naïf qui les tasserait au centre.
 *
 * Les tailles suivent une loi décroissante : beaucoup de petits, quelques grands. Un
 * champ de cratères de taille uniforme se lit comme un motif, pas comme une surface.
 */
object CraterField {

    private const val SEED = 424_242L
    private const val COUNT = 14

    /** Marge au bord : un cratère à cheval sur le limbe trahirait le dessin en 2D. */
    private const val MAX_DISTANCE = 0.78f

    fun generate(): List<Crater> {
        val random = Random(SEED)
        val craters = mutableListOf<Crater>()
        var attempts = 0

        while (craters.size < COUNT && attempts < COUNT * 40) {
            attempts++
            val x = random.nextFloat() * 2f - 1f
            val y = random.nextFloat() * 2f - 1f
            val distance = sqrt(x * x + y * y)
            if (distance > MAX_DISTANCE) continue

            val roll = random.nextFloat()
            val radius = 0.055f + roll * roll * 0.19f
            if (craters.any { overlaps(it, x, y, radius) }) continue

            craters += Crater(x = x, y = y, radius = radius, depth = 0.55f + random.nextFloat() * 0.45f)
        }
        return craters
    }

    private fun overlaps(existing: Crater, x: Float, y: Float, radius: Float): Boolean {
        val dx = existing.x - x
        val dy = existing.y - y
        return sqrt(dx * dx + dy * dy) < (existing.radius + radius) * 0.9f
    }
}
