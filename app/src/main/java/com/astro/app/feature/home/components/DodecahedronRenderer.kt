package com.astro.app.feature.home.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.astro.app.core.designsystem.texture.lerp
import com.astro.app.feature.home.DodecahedronFaces
import com.astro.app.feature.home.DodecahedronGeometry
import com.astro.app.feature.home.DodecahedronGeometry.Vertex

/** Opacité des faces. En dessous de 1, le solide garde sa transparence de relique. */
private const val FACE_ALPHA = 0.82f

/**
 * Dessine le solide comme un objet plein éclairé, et non comme un fil de fer.
 *
 * Trois passes :
 *  1. les faces tournées vers l'observateur sont remplies, chacune teintée selon son
 *     éclairement — c'est ce qui donne le volume ;
 *  2. leurs contours sont repris finement, ce qui détache les facettes les unes des autres ;
 *  3. les arêtes cachées sont laissées visibles mais très pâles : l'objet garde ainsi
 *     la lecture d'une relique translucide plutôt que d'un caillou opaque.
 *
 * Les faces sont triées par profondeur avant remplissage : sans ce tri, une face
 * lointaine tracée en dernier recouvrirait une face proche.
 */
internal fun DrawScope.drawDodecahedron(
    yaw: Float,
    pitch: Float,
    roll: Float,
    center: Offset,
    scale: Float,
    strokeWidth: Float,
    darkFace: Color,
    litFace: Color,
    edge: Color,
) {
    val rotated = DodecahedronGeometry.vertices.map { rotate(it, yaw, pitch, roll) }
    val screen = rotated.map { toScreen(it, center, scale) }

    DodecahedronGeometry.edges.forEach { (from, to) ->
        val depth = (rotated[from].z + rotated[to].z) / 2f
        drawLine(
            color = edge.copy(alpha = 0.16f),
            start = screen[from],
            end = screen[to],
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round,
        )
    }

    DodecahedronFaces.all
        .map { face -> face to rotate(face.normal, yaw, pitch, roll) }
        .filter { (_, normal) -> isFacingViewer(normal) }
        .sortedBy { (_, normal) -> normal.z }
        .forEach { (face, normal) ->
            drawFace(face.indices, screen, normal, darkFace, litFace, edge, strokeWidth)
        }
}

private fun DrawScope.drawFace(
    indices: List<Int>,
    screen: List<Offset>,
    normal: Vertex,
    darkFace: Color,
    litFace: Color,
    edge: Color,
    strokeWidth: Float,
) {
    val path = Path().apply {
        moveTo(screen[indices.first()].x, screen[indices.first()].y)
        indices.drop(1).forEach { lineTo(screen[it].x, screen[it].y) }
        close()
    }
    // L'éclairement est adouci : une loi lambertienne brute écrase les faces obliques
    // dans le noir et donne un rendu dur, contraire à l'ambiance recherchée.
    val light = 0.22f + 0.78f * lambert(normal)
    val fill = lerp(darkFace, litFace, light).copy(alpha = FACE_ALPHA * (0.55f + 0.45f * normal.z))

    drawPath(path = path, brush = Brush.linearGradient(listOf(fill, fill.copy(alpha = fill.alpha * 0.75f))))
    drawPath(
        path = path,
        color = edge.copy(alpha = 0.30f + 0.55f * light),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
    )
}
