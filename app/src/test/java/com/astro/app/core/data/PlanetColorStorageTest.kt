package com.astro.app.core.data

import com.astro.app.core.settings.KeywordColorRule
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Le format de sérialisation doit survivre à des mots contenant n'importe quel
 * caractère : c'est ce que vérifient ces tests.
 */
class PlanetColorStorageTest {

    private fun roundTrip(rules: List<KeywordColorRule>) = decodeRules(encodeRules(rules))

    @Test
    fun `une regle simple survit a l'aller-retour`() {
        val rules = listOf(KeywordColorRule("sport", 0xFF123456.toInt()))
        assertEquals(rules, roundTrip(rules))
    }

    @Test
    fun `un mot contenant des separateurs habituels survit`() {
        // Le format place la couleur en préfixe de longueur fixe : aucun séparateur
        // entre les champs, donc aucun caractère du mot ne peut casser la lecture.
        val rules = listOf(KeywordColorRule("a|b=c,d;e", 0xFFABCDEF.toInt()))
        assertEquals(rules, roundTrip(rules))
    }

    @Test
    fun `un retour a la ligne est neutralise`() {
        val decoded = roundTrip(listOf(KeywordColorRule("deux\nlignes", 0xFF000000.toInt())))
        assertEquals(1, decoded.size)
        assertEquals("deux lignes", decoded.first().keyword)
    }

    @Test
    fun `plusieurs regles conservent leur ordre`() {
        val rules = listOf(
            KeywordColorRule("un", 0xFF111111.toInt()),
            KeywordColorRule("deux", 0xFF222222.toInt()),
            KeywordColorRule("trois", 0xFF333333.toInt()),
        )
        assertEquals(rules, roundTrip(rules))
    }

    @Test
    fun `une regle au mot vide n'est pas enregistree`() {
        assertEquals(emptyList<KeywordColorRule>(), roundTrip(listOf(KeywordColorRule("  ", 1))))
    }

    @Test
    fun `une couleur avec alpha maximal n'est pas tronquee`() {
        val rules = listOf(KeywordColorRule("nuit", 0xFFFFFFFF.toInt()))
        assertEquals(rules, roundTrip(rules))
    }
}
