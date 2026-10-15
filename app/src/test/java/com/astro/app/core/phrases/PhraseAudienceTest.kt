package com.astro.app.core.phrases

import com.astro.app.core.phrases.PhraseGender.FEMININE
import com.astro.app.core.phrases.PhraseGender.MASCULINE
import com.astro.app.core.phrases.PhraseGender.NEUTRAL
import org.junit.Assert.assertEquals
import org.junit.Test

class PhraseAudienceTest {

    private val fier = Phrase("Je suis fier.", theme = "Confiance", gender = MASCULINE)
    private val fiere = Phrase("Je suis fière.", theme = "Confiance", gender = FEMININE)
    private val calme = Phrase("Je respire.", theme = "Paix", gender = NEUTRAL)
    private val libre = Phrase("Sans thème.")
    private val all = listOf(fier, fiere, calme, libre)

    @Test
    fun `masculin recoit neutre et masculin`() {
        assertEquals(listOf(fier, calme, libre), PhraseAudience.eligible(all, ReaderGender.MASCULINE, emptySet()))
    }

    @Test
    fun `feminin recoit neutre et feminin`() {
        assertEquals(listOf(fiere, calme, libre), PhraseAudience.eligible(all, ReaderGender.FEMININE, emptySet()))
    }

    @Test
    fun `autre, ou rien de renseigne, recoit le neutre seulement`() {
        assertEquals(listOf(calme, libre), PhraseAudience.eligible(all, ReaderGender.OTHER, emptySet()))
        assertEquals(listOf(calme, libre), PhraseAudience.eligible(all, null, emptySet()))
    }

    @Test
    fun `themes choisis, sans casse ni espaces, et une phrase sans theme n'y est pas`() {
        assertEquals(listOf(calme), PhraseAudience.eligible(all, ReaderGender.OTHER, setOf(" paix ")))
        assertEquals(listOf(fiere, calme), PhraseAudience.eligible(all, ReaderGender.FEMININE, setOf("Paix", "CONFIANCE")))
    }

    @Test
    fun `themes distincts tries`() {
        assertEquals(listOf("Confiance", "Paix"), PhraseAudience.themes(all))
    }
}
