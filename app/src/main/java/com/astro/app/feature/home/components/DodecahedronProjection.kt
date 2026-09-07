package com.astro.app.feature.home.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.astro.app.feature.home.DodecahedronGeometry.Vertex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Mathématiques de rendu du solide : rotations, perspective et éclairage.
 * Séparé du composant, parce que calculer et dessiner sont deux responsabilités.
 */

/** Distance de l'œil au centre du solide, en rayons. Plus elle est faible, plus la
 * perspective est marquée — trop faible, la figure se déforme jusqu'à l'illisible. */
private const val EYE_DISTANCE = 3.4f

/**
 * Direction de la lumière dans le repère du solide.
 *
 * Elle reprend la source commune de l'application — en haut à droite — avec une
 * composante vers l'observateur, sans quoi les faces de face resteraient toutes sombres.
 * L'axe Y est ici vers le haut, contrairement à celui de l'écran.
 */
private val LIGHT = normalise(Vertex(0.42f, 0.46f, 0.78f))

/**
 * Applique les trois rotations : axe vertical, axe horizontal, puis roulis.
 *
 * La même transformation sert aux sommets et aux normales des faces — c'est ce qui
 * garantit que l'éclairage suit réellement l'orientation du solide.
 */
internal fun rotate(vertex: Vertex, yawDegrees: Float, pitchDegrees: Float, rollDegrees: Float): Vertex {
    val yaw = yawDegrees * PI.toFloat() / 180f
    val pitch = pitchDegrees * PI.toFloat() / 180f
    val roll = rollDegrees * PI.toFloat() / 180f

    val x1 = vertex.x * cos(yaw) + vertex.z * sin(yaw)
    val z1 = -vertex.x * sin(yaw) + vertex.z * cos(yaw)
    val y2 = vertex.y * cos(pitch) - z1 * sin(pitch)
    val z2 = vertex.y * sin(pitch) + z1 * cos(pitch)

    // Roulis : rotation dans le plan de l'écran, elle ne modifie pas la profondeur.
    return Vertex(
        x = x1 * cos(roll) - y2 * sin(roll),
        y = x1 * sin(roll) + y2 * cos(roll),
        z = z2,
    )
}

/**
 * Projette un sommet déjà tourné.
 * La division par la distance à l'œil est ce qui produit la perspective.
 */
internal fun toScreen(rotated: Vertex, center: Offset, scale: Float): Offset {
    val depthFactor = EYE_DISTANCE / (EYE_DISTANCE - rotated.z)
    // L'axe Y de l'écran est inversé par rapport au repère 3D.
    return Offset(
        x = center.x + rotated.x * scale * depthFactor,
        y = center.y - rotated.y * scale * depthFactor,
    )
}

/** Une face est visible si sa normale pointe vers l'observateur, placé du côté des z positifs. */
internal fun isFacingViewer(normal: Vertex): Boolean = normal.z > 0f

/** Éclairement lambertien d'une face, dans [0, 1]. */
internal fun lambert(normal: Vertex): Float =
    (normal.x * LIGHT.x + normal.y * LIGHT.y + normal.z * LIGHT.z).coerceIn(0f, 1f)

/** Une arête éloignée est plus pâle : c'est ce qui donne la lecture du volume. */
internal fun alphaForDepth(depth: Float): Float =
    0.22f + 0.78f * ((depth + 1f) / 2f).coerceIn(0f, 1f)

/** Les arêtes proches virent vers l'accent cosmique, les lointaines vers le lunaire. */
internal fun lerpEdgeColor(near: Color, far: Color, depth: Float): Color {
    val t = ((depth + 1f) / 2f).coerceIn(0f, 1f)
    return Color(
        red = far.red + (near.red - far.red) * t,
        green = far.green + (near.green - far.green) * t,
        blue = far.blue + (near.blue - far.blue) * t,
    )
}

private fun normalise(v: Vertex): Vertex {
    val length = kotlin.math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
    return Vertex(v.x / length, v.y / length, v.z / length)
}
