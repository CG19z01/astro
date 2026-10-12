package com.astro.app.core.phrases

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DailyPhrasePickerTest {

    private val day = LocalDate.of(2026, 10, 11)
    private val phrases = (1..5).map { Phrase("Phrase $it") }

    @Test
    fun `sans liste, aucune phrase`() {
        assertNull(DailyPhrasePicker.pick(day, emptyList(), emptyMap()))
    }

    @Test
    fun `meme date, meme phrase`() {
        assertEquals(DailyPhrasePicker.pick(day, phrases, emptyMap()), DailyPhrasePicker.pick(day, phrases, emptyMap()))
    }

    @Test
    fun `une journee inscrite garde sa phrase meme si la liste a change`() {
        val old = Phrase("Ancienne")
        assertEquals(old, DailyPhrasePicker.pick(day, phrases, mapOf(day to old)))
    }

    @Test
    fun `toutes les phrases passent avant qu'une revienne`() {
        val journal = mutableMapOf<LocalDate, Phrase>()
        repeat(phrases.size) { offset ->
            val date = day.plusDays(offset.toLong())
            journal[date] = DailyPhrasePicker.pick(date, phrases, journal)!!
        }
        assertEquals(phrases.toSet(), journal.values.toSet())
    }

    @Test
    fun `les jours consecutifs ne suivent pas l'ordre du fichier`() {
        val picks = (0L..4L).map { DailyPhrasePicker.pick(day.plusDays(it), phrases, emptyMap()) }
        val inFileOrder = picks.zipWithNext().all { (a, b) -> phrases.indexOf(b) == phrases.indexOf(a) + 1 }
        assertEquals(false, inFileOrder)
    }
}
