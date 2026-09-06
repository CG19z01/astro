package com.astro.app.core.settings

import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureStatesTest {

    @Test
    fun `sans reglage stocke, on retombe sur les valeurs par defaut du catalogue`() {
        val merged = FeatureStates.merge(emptyMap())
        FeatureCatalog.definitions.forEach { definition ->
            assertEquals(definition.defaultEnabled, merged[definition.id])
        }
    }

    @Test
    fun `un reglage stocke prend le pas sur la valeur par defaut`() {
        val merged = FeatureStates.merge(mapOf(FeatureId.ASTROLOGY to false))
        assertFalse(merged.getValue(FeatureId.ASTROLOGY))
    }

    @Test
    fun `une fonctionnalite non desactivable reste activee malgre un stockage a false`() {
        val merged = FeatureStates.merge(
            mapOf(FeatureId.HOME to false, FeatureId.SETTINGS to false),
        )
        assertTrue(merged.getValue(FeatureId.HOME))
        assertTrue(merged.getValue(FeatureId.SETTINGS))
    }

    @Test
    fun `le resultat couvre toujours toutes les fonctionnalites du catalogue`() {
        val merged = FeatureStates.merge(mapOf(FeatureId.CALENDAR to false))
        assertEquals(FeatureCatalog.definitions.size, merged.size)
    }

    @Test
    fun `une fonctionnalite desactivee disparait de la barre basse`() {
        val states = FeatureStates.merge(mapOf(FeatureId.CALENDAR to false))
        val visible = FeatureStates.visibleInBottomBar(states)
        assertFalse(visible.any { it.id == FeatureId.CALENDAR })
        assertEquals(FeatureCatalog.bottomBarFeatures.size - 1, visible.size)
    }

    @Test
    fun `une route desactivee ou inconnue n est pas atteignable`() {
        val states = FeatureStates.merge(mapOf(FeatureId.PHRASES to false))
        val phrasesRoute = FeatureCatalog.definitionOf(FeatureId.PHRASES).route
        assertFalse(FeatureStates.isRouteReachable(states, phrasesRoute))
        assertFalse(FeatureStates.isRouteReachable(states, "route_inexistante"))
        assertTrue(FeatureStates.isRouteReachable(states, FeatureCatalog.home.route))
    }
}
