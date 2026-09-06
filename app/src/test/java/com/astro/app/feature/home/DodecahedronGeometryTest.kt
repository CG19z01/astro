package com.astro.app.feature.home

import kotlin.math.abs
import kotlin.math.sqrt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Les arêtes étant déduites des distances et non listées à la main, ces tests sont
 * ce qui garantit que le solide dessiné est bien un dodécaèdre régulier.
 */
class DodecahedronGeometryTest {

    private val tolerance = 1e-4f

    @Test
    fun `le solide a 20 sommets`() {
        assertEquals(20, DodecahedronGeometry.vertices.size)
    }

    @Test
    fun `le solide a 30 aretes`() {
        assertEquals(30, DodecahedronGeometry.edges.size)
    }

    @Test
    fun `la relation d'Euler est verifiee pour 12 faces`() {
        val v = DodecahedronGeometry.vertices.size
        val e = DodecahedronGeometry.edges.size
        // S - A + F = 2  =>  F = 2 - S + A
        assertEquals(12, 2 - v + e)
    }

    @Test
    fun `tous les sommets sont sur la sphere unite`() {
        DodecahedronGeometry.vertices.forEach { vertex ->
            val norm = sqrt(vertex.x * vertex.x + vertex.y * vertex.y + vertex.z * vertex.z)
            assertTrue("norme = $norm", abs(norm - 1f) < tolerance)
        }
    }

    @Test
    fun `toutes les aretes ont la meme longueur`() {
        val lengths = DodecahedronGeometry.edges.map { (i, j) ->
            val a = DodecahedronGeometry.vertices[i]
            val b = DodecahedronGeometry.vertices[j]
            sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z))
        }
        val first = lengths.first()
        lengths.forEach { assertTrue("longueurs inégales", abs(it - first) < tolerance) }
    }

    @Test
    fun `chaque sommet touche exactement trois aretes`() {
        val degrees = IntArray(DodecahedronGeometry.vertices.size)
        DodecahedronGeometry.edges.forEach { (i, j) -> degrees[i]++; degrees[j]++ }
        degrees.forEachIndexed { index, degree ->
            assertEquals("sommet $index", 3, degree)
        }
    }

    @Test
    fun `aucun sommet n'est en double`() {
        val distinct = DodecahedronGeometry.vertices.distinct()
        assertEquals(DodecahedronGeometry.vertices.size, distinct.size)
    }
}
