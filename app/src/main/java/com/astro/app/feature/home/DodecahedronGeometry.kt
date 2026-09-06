package com.astro.app.feature.home

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Géométrie d'un dodécaèdre régulier : 20 sommets, 30 arêtes, 12 faces pentagonales.
 *
 * Les sommets sont construits à partir du nombre d'or, selon la description classique
 * du solide : les 8 sommets d'un cube, plus trois rectangles d'or dans les plans
 * de coordonnées.
 *
 * Les arêtes ne sont pas listées à la main : elles sont déduites des distances. Dans
 * un dodécaèdre régulier, deux sommets voisins sont exactement à une longueur d'arête,
 * et la distance suivante — la diagonale d'une face — est nettement plus grande.
 * Énumérer 30 paires d'indices à la main aurait été une source d'erreurs silencieuses.
 *
 * Objet pur, sans dépendance à Android : sa correction est vérifiée par test unitaire.
 */
object DodecahedronGeometry {

    data class Vertex(val x: Float, val y: Float, val z: Float)

    private val PHI = ((1.0 + sqrt(5.0)) / 2.0).toFloat()
    private val INV_PHI = 1f / PHI

    /** Rayon de la sphère circonscrite pour les coordonnées brutes ci-dessous. */
    private val CIRCUMRADIUS = sqrt(3f)

    /** Sommets ramenés sur la sphère unité, pour tenir dans le cadre de dessin. */
    val vertices: List<Vertex> = buildRawVertices()
        .map { Vertex(it.x / CIRCUMRADIUS, it.y / CIRCUMRADIUS, it.z / CIRCUMRADIUS) }

    /** Les 30 arêtes, sous forme de paires d'indices dans [vertices]. */
    val edges: List<Pair<Int, Int>> = buildEdges(vertices)

    private fun buildRawVertices(): List<Vertex> {
        val signs = listOf(1f, -1f)
        val cube = signs.flatMap { x -> signs.flatMap { y -> signs.map { z -> Vertex(x, y, z) } } }
        // Trois rectangles d'or, un par plan de coordonnées.
        val goldenRectangles = signs.flatMap { a ->
            signs.flatMap { b ->
                listOf(
                    Vertex(0f, a * INV_PHI, b * PHI),
                    Vertex(a * INV_PHI, b * PHI, 0f),
                    Vertex(a * PHI, 0f, b * INV_PHI),
                )
            }
        }
        return cube + goldenRectangles
    }

    private fun buildEdges(points: List<Vertex>): List<Pair<Int, Int>> {
        // Longueur d'arête du solide brut, ramenée à la même échelle que les sommets.
        val edgeLength = (2f * INV_PHI) / CIRCUMRADIUS
        // La distance suivante vaut environ 1,6 fois celle-ci : la tolérance est confortable.
        val tolerance = edgeLength * 0.05f

        val found = mutableListOf<Pair<Int, Int>>()
        for (i in points.indices) {
            for (j in i + 1 until points.size) {
                if (abs(distance(points[i], points[j]) - edgeLength) < tolerance) {
                    found += i to j
                }
            }
        }
        return found
    }

    private fun distance(a: Vertex, b: Vertex): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}
