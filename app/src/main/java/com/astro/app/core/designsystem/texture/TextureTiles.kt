package com.astro.app.core.designsystem.texture

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb

/**
 * Tuiles de matière, calculées une fois puis répétées.
 *
 * Pourquoi calculer plutôt que charger des images : une tuile calculée s'adapte à la
 * palette de l'ambiance courante, se décline par une simple graine — ce qui permet de
 * varier l'aspect d'un élément à l'autre sans rompre la cohérence — et évite d'ajouter
 * des dizaines de fichiers au projet. Les tuiles sont petites et mises en cache : le
 * coût est payé une seule fois.
 *
 * Pour passer un jour à des ressources WebP, seul ce fichier serait à remplacer.
 */
object TextureTiles {

    /** Côté de la tuile de grain, en pixels. Petit : elle est répétée. */
    const val GRAIN_SIZE = 96

    /** Côté de la tuile de poussière. Encore plus petite : elle sera fortement agrandie. */
    const val DUST_SIZE = 48
}

/** Grain fin : bruit blanc en niveaux d'alpha. Donne la matière la plus discrète. */
@Composable
fun rememberGrainBrush(color: Color, intensity: Float): ShaderBrush {
    val tile = remember(color, intensity) { grainTile(color, intensity) }
    return remember(tile) { ShaderBrush(ImageShader(tile, TileMode.Repeated, TileMode.Repeated)) }
}

/**
 * Poussière cosmique : bruit fractal très étalé.
 * Le facteur d'agrandissement adoucit la tuile jusqu'à la rendre nuageuse.
 */
@Composable
fun rememberDustBrush(color: Color, intensity: Float, seed: Int): ShaderBrush {
    val tile = remember(color, intensity, seed) { dustTile(color, intensity, seed) }
    return remember(tile) { ShaderBrush(ImageShader(tile, TileMode.Repeated, TileMode.Repeated)) }
}

private fun grainTile(color: Color, intensity: Float): ImageBitmap {
    val size = TextureTiles.GRAIN_SIZE
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        for (x in 0 until size) {
            val value = ValueNoise.white(x, y, size, seed = 1)
            pixels[y * size + x] = color.copy(alpha = value * intensity).toArgb()
        }
    }
    return toBitmap(pixels, size)
}

private fun dustTile(color: Color, intensity: Float, seed: Int): ImageBitmap {
    val size = TextureTiles.DUST_SIZE
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        for (x in 0 until size) {
            // Le bruit fractal est recentré : on ne garde que les crêtes, ce qui donne
            // des nuages détachés au lieu d'un voile uniforme.
            val raw = ValueNoise.fractal(x / 8f, y / 8f, period = size / 8, seed = seed, octaves = 3)
            val value = ((raw - 0.45f) / 0.55f).coerceIn(0f, 1f)
            pixels[y * size + x] = color.copy(alpha = value * intensity).toArgb()
        }
    }
    return toBitmap(pixels, size)
}

private fun toBitmap(pixels: IntArray, size: Int): ImageBitmap =
    Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888).asImageBitmap()
