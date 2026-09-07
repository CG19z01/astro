package com.astro.app.feature.home

import kotlin.math.abs
import kotlin.math.sqrt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Les faces étant déduites des normales et non listées à la main, ces tests sont ce
 * qui garantit que le solide rempli est bien un dodécaèdre et non un amas de polygones.
 */
class DodecahedronFacesTest {

    private val tolerance = 1e-3f

    @Test
    fun `le solide a 12 faces`() {
        assertEquals(12, DodecahedronFaces.all.size)
    }

    @Test
    fun `chaque face est un pentagone`() {
        DodecahedronFaces.all.forEach { assertEquals(5, it.indices.size) }
    }

    @Test
    fun `une face ne contient jamais deux fois le meme sommet`() {
        DodecahedronFaces.all.forEach {
            assertEquals(it.indices.size, it.indices.toSet().size)
        }
    }

    @Test
    fun `chaque sommet appartient a exactement trois faces`() {
        val counts = IntArray(DodecahedronGeometry.vertices.size)
        DodecahedronFaces.all.forEach { face -> face.indices.forEach { counts[it]++ } }
        counts.forEachIndexed { index, count -> assertEquals("sommet $index", 3, count) }
    }

    @Test
    fun `les normales sont unitaires`() {
        DodecahedronFaces.all.forEach { face ->
            val n = face.normal
            val length = sqrt(n.x * n.x + n.y * n.y + n.z * n.z)
            assertTrue("norme = $length", abs(length - 1f) < tolerance)
        }
    }

    @Test
    fun `la normale d'une face pointe vers l'exterieur depuis son centre`() {
        DodecahedronFaces.all.forEach { face ->
            val points = face.indices.map { DodecahedronGeometry.vertices[it] }
            val cx = points.map { it.x }.average().toFloat()
            val cy = points.map { it.y }.average().toFloat()
            val cz = points.map { it.z }.average().toFloat()
            val outward = cx * face.normal.x + cy * face.normal.y + cz * face.normal.z
            assertTrue("normale rentrante", outward > 0f)
        }
    }

    @Test
    fun `les sommets d'une face sont ordonnes en polygone simple`() {
        // Deux sommets consécutifs d'un pentagone régulier sont toujours à une longueur
        // d'arête. Un ordre incorrect produirait une diagonale, donc une distance plus grande.
        val edgeLength = (2f / ((1f + sqrt(5f)) / 2f)) / sqrt(3f)
        DodecahedronFaces.all.forEach { face ->
            face.indices.indices.forEach { position ->
                val a = DodecahedronGeometry.vertices[face.indices[position]]
                val b = DodecahedronGeometry.vertices[face.indices[(position + 1) % face.indices.size]]
                val dx = a.x - b.x
                val dy = a.y - b.y
                val dz = a.z - b.z
                val distance = sqrt(dx * dx + dy * dy + dz * dz)
                assertTrue("côté de longueur $distance", abs(distance - edgeLength) < tolerance)
            }
        }
    }
}
