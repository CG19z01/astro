package com.astro.app.core.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Garde-fous sur le catalogue : ce sont les invariants dont dépend toute la navigation. */
class FeatureCatalogTest {

    @Test
    fun `chaque fonctionnalite declaree a une entree dans le catalogue`() {
        FeatureId.entries.forEach { id ->
            assertNotNull("Aucune définition pour $id", FeatureCatalog.definitionOf(id))
        }
    }

    @Test
    fun `les routes sont uniques`() {
        val routes = FeatureCatalog.definitions.map { it.route }
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun `les routes ne sont pas vides`() {
        FeatureCatalog.definitions.forEach { assertTrue(it.route.isNotBlank()) }
    }

    @Test
    fun `la Home et les Parametres ne peuvent pas etre desactives`() {
        assertTrue(!FeatureCatalog.definitionOf(FeatureId.HOME).canBeDisabled)
        assertTrue(!FeatureCatalog.definitionOf(FeatureId.SETTINGS).canBeDisabled)
    }

    @Test
    fun `la barre basse compte cinq entrees, conformement a la maquette`() {
        assertEquals(5, FeatureCatalog.bottomBarFeatures.size)
    }

    @Test
    fun `la Manifestation n est pas dans la barre basse`() {
        assertTrue(!FeatureCatalog.definitionOf(FeatureId.MANIFESTATION).inBottomBar)
    }

    @Test
    fun `une route inconnue ne resout aucune definition`() {
        assertNull(FeatureCatalog.definitionOfRouteOrNull("route_inexistante"))
        assertNull(FeatureCatalog.definitionOfRouteOrNull(null))
    }
}
