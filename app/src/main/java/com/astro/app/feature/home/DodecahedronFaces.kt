package com.astro.app.feature.home

import com.astro.app.feature.home.DodecahedronGeometry.Vertex
import kotlin.math.atan2
import kotlin.math.sqrt

/** Une face pentagonale : ses sommets ordonnés, et sa normale sortante. */
data class Face(val indices: List<Int>, val normal: Vertex)

/**
 * Les 12 faces pentagonales du dodécaèdre.
 *
 * Elles ne sont pas listées à la main. Les normales des faces d'un dodécaèdre pointent
 * vers les sommets de l'icosaèdre dual ; pour chacune de ces 12 directions, la face est
 * l'ensemble des 5 sommets les plus avancés dans cette direction.
 *
 * Les sommets sont ensuite ordonnés par angle autour de la normale, sans quoi le
 * polygone tracé se replierait sur lui-même au lieu de former un pentagone.
 *
 * Objet pur : ses propriétés sont vérifiées par test unitaire.
 */
object DodecahedronFaces {

    private const val VERTICES_PER_FACE = 5

    val all: List<Face> by lazy { build() }

    private fun build(): List<Face> = normals().map { normal ->
        val ordered = DodecahedronGeometry.vertices.indices
            .sortedByDescending { dot(DodecahedronGeometry.vertices[it], normal) }
            .take(VERTICES_PER_FACE)
            .let { orderAround(it, normal) }
        Face(ordered, normal)
    }

    /**
     * Directions des centres des 12 faces, ramenées sur la sphère unité.
     *
     * Attention au piège : ce sont les permutations de (0, ±φ, ±1), et non celles de
     * (0, ±1, ±φ) qui donnent les sommets de l'icosaèdre. Avec ces dernières, les cinq
     * sommets retenus ne sont pas coplanaires et le pentagone est faux.
     */
    private fun normals(): List<Vertex> {
        val phi = ((1.0 + sqrt(5.0)) / 2.0).toFloat()
        val signs = listOf(1f, -1f)
        return signs.flatMap { a ->
            signs.flatMap { b ->
                listOf(
                    Vertex(0f, a * phi, b),
                    Vertex(a * phi, b, 0f),
                    Vertex(a, 0f, b * phi),
                )
            }
        }.map { normalise(it) }
    }

    /** Trie les sommets par angle autour de la normale, dans un repère local à la face. */
    private fun orderAround(indices: List<Int>, normal: Vertex): List<Int> {
        val reference = DodecahedronGeometry.vertices[indices.first()]
        val u = normalise(reject(reference, normal))
        val w = cross(normal, u)
        return indices.sortedBy { index ->
            val v = DodecahedronGeometry.vertices[index]
            atan2(dot(v, w), dot(v, u))
        }
    }

    private fun dot(a: Vertex, b: Vertex) = a.x * b.x + a.y * b.y + a.z * b.z

    private fun cross(a: Vertex, b: Vertex) = Vertex(
        a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x,
    )

    /** Composante de [v] perpendiculaire à [axis]. */
    private fun reject(v: Vertex, axis: Vertex): Vertex {
        val projection = dot(v, axis)
        return Vertex(v.x - axis.x * projection, v.y - axis.y * projection, v.z - axis.z * projection)
    }

    private fun normalise(v: Vertex): Vertex {
        val length = sqrt(dot(v, v))
        return Vertex(v.x / length, v.y / length, v.z / length)
    }
}
