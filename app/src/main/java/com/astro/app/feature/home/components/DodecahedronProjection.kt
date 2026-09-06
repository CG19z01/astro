package com.astro.app.feature.home.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.astro.app.feature.home.DodecahedronGeometry.Vertex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Mathématiques de rendu du dodécaèdre : rotations, perspective et lecture de la
 * profondeur. Séparé du composant pour tenir la limite de 150 lignes par fichier,
 * et parce que ce sont deux responsabilités distinctes — calculer et dessiner.
 */

/** Distance de l'œil au centre du solide, en rayons. Plus elle est faible, plus la
 * perspective est marquée — trop faible, la figure se déforme jusqu'à l'illisible. */
private const val EYE_DISTANCE = 3.4f

/**
 * Projette un sommet et renvoie sa position écran ainsi que sa profondeur.
 *
 * Trois rotations enchaînées : axe vertical, axe horizontal, puis roulis autour de
 * l'axe qui pointe vers l'observateur. Le roulis est à zéro au repos et n'entre en jeu
 * que pendant l'accélération : c'est lui qui fait changer le solide d'axe.
 *
 * La division par la distance à l'œil est ce qui produit la perspective.
 */
internal fun project(
    vertex: Vertex,
    yawDegrees: Float,
    pitchDegrees: Float,
    rollDegrees: Float,
    center: Offset,
    scale: Float,
): Pair<Offset, Float> {
    val yaw = yawDegrees * PI.toFloat() / 180f
    val pitch = pitchDegrees * PI.toFloat() / 180f
    val roll = rollDegrees * PI.toFloat() / 180f

    val x1 = vertex.x * cos(yaw) + vertex.z * sin(yaw)
    val z1 = -vertex.x * sin(yaw) + vertex.z * cos(yaw)
    val y2 = vertex.y * cos(pitch) - z1 * sin(pitch)
    val z2 = vertex.y * sin(pitch) + z1 * cos(pitch)

    // Roulis : rotation dans le plan de l'écran, elle ne modifie donc pas la profondeur.
    val x3 = x1 * cos(roll) - y2 * sin(roll)
    val y3 = x1 * sin(roll) + y2 * cos(roll)

    // Perspective : l'œil est placé à EYE_DISTANCE rayons du centre du solide.
    val depthFactor = EYE_DISTANCE / (EYE_DISTANCE - z2)

    // L'axe Y de l'écran est inversé par rapport au repère 3D.
    return Offset(
        x = center.x + x3 * scale * depthFactor,
        y = center.y - y3 * scale * depthFactor,
    ) to z2
}

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
