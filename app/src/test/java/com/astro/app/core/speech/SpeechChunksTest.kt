package com.astro.app.core.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeechChunksTest {

    @Test
    fun `un texte court reste entier`() {
        assertEquals(listOf("Je suis calme."), SpeechChunks.split("  Je suis calme.  ", 4000))
    }

    @Test
    fun `on coupe apres une fin de phrase`() {
        val chunks = SpeechChunks.split("Je suis calme. Je suis forte. Je suis libre.", 32)
        assertEquals(listOf("Je suis calme. Je suis forte.", "Je suis libre."), chunks)
    }

    @Test
    fun `on prefere un paragraphe`() {
        val chunks = SpeechChunks.split("Un. Deux.\n\nTrois quatre cinq.", 18)
        assertEquals(listOf("Un. Deux.", "Trois quatre cinq."), chunks)
    }

    @Test
    fun `jamais au milieu d'un mot s'il y a un espace`() {
        val chunks = SpeechChunks.split("abondance gratitude sérénité", 12)
        assertEquals(listOf("abondance", "gratitude", "sérénité"), chunks)
    }

    @Test
    fun `aucun morceau ne depasse la limite et rien n'est perdu`() {
        val text = (1..400).joinToString(" ") { "Phrase numéro $it." }
        val chunks = SpeechChunks.split(text, 4000)
        assertTrue(chunks.all { it.length <= 4000 })
        assertEquals(text.replace(" ", ""), chunks.joinToString("").replace(" ", ""))
    }

    @Test
    fun `texte vide, rien a lire`() {
        assertEquals(emptyList<String>(), SpeechChunks.split("   ", 10))
    }
}
