package com.astro.app.core.designsystem.texture

import kotlin.math.floor

/**
 * Bruit de valeur périodique, utilisé pour fabriquer les matières.
 *
 * **Périodique** est la propriété essentielle : les tuiles produites sont répétées sur
 * tout l'écran, et une tuile non périodique laisserait apparaître une grille de
 * raccords. Les coordonnées du réseau sont donc ramenées modulo la période.
 *
 * Objet pur, sans dépendance à Android : ses propriétés sont vérifiées par test unitaire.
 */
object ValueNoise {

    /** Bruit blanc : valeur indépendante par cellule. Sert au grain fin. */
    fun white(x: Int, y: Int, period: Int, seed: Int): Float =
        hash(x.mod(period), y.mod(period), seed)

    /**
     * Bruit lissé : interpolation douce entre les valeurs du réseau.
     * Sert aux variations larges — brume, poussière.
     */
    fun smooth(x: Float, y: Float, period: Int, seed: Int): Float {
        val x0 = floor(x).toInt()
        val y0 = floor(y).toInt()
        val fx = fade(x - x0)
        val fy = fade(y - y0)

        val topLeft = white(x0, y0, period, seed)
        val topRight = white(x0 + 1, y0, period, seed)
        val bottomLeft = white(x0, y0 + 1, period, seed)
        val bottomRight = white(x0 + 1, y0 + 1, period, seed)

        val top = topLeft + (topRight - topLeft) * fx
        val bottom = bottomLeft + (bottomRight - bottomLeft) * fx
        return top + (bottom - top) * fy
    }

    /**
     * Somme de plusieurs octaves de bruit lissé.
     *
     * Chaque octave double la fréquence et divise l'amplitude : c'est ce qui donne une
     * matière qui a du détail à plusieurs échelles au lieu d'un flou uniforme.
     */
    fun fractal(x: Float, y: Float, period: Int, seed: Int, octaves: Int): Float {
        var total = 0f
        var amplitude = 1f
        var frequency = 1f
        var normalisation = 0f

        repeat(octaves) { octave ->
            total += smooth(x * frequency, y * frequency, period * frequency.toInt(), seed + octave) * amplitude
            normalisation += amplitude
            amplitude *= 0.5f
            frequency *= 2f
        }
        return total / normalisation
    }

    /** Fonction d'atténuation de Perlin : dérivées nulles aux bords, donc pas d'arête. */
    private fun fade(t: Float): Float = t * t * t * (t * (t * 6f - 15f) + 10f)

    /** Hachage entier déterministe, ramené dans [0, 1]. */
    private fun hash(x: Int, y: Int, seed: Int): Float {
        var h = x * 374_761_393 + y * 668_265_263 + seed * 1_274_126_177
        h = (h xor (h shr 13)) * 1_274_126_177
        h = h xor (h shr 16)
        return (h and 0x7FFFFFFF) / 0x7FFFFFFF.toFloat()
    }
}
