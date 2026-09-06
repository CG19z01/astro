package com.astro.app.core.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Contour dentelé fermé : alternance de sommets sur un rayon externe et un rayon interne.
 *
 * Sert à la fois à la couronne du Soleil sur la page d'accueil et à l'engrenage des
 * paramètres dans la barre de navigation : c'est la même figure à deux réglages près.
 *
 * @param teeth nombre de pointes.
 * @param innerRatio rayon des creux, en fraction du rayon externe.
 */
fun toothedPath(center: Offset, outerRadius: Float, teeth: Int, innerRatio: Float): Path {
    val path = Path()
    val steps = teeth * 2
    for (index in 0 until steps) {
        val angle = (index.toDouble() / steps) * 2.0 * PI - PI / 2.0
        val length = if (index % 2 == 0) outerRadius else outerRadius * innerRatio
        val x = center.x + (cos(angle) * length).toFloat()
        val y = center.y + (sin(angle) * length).toFloat()
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
