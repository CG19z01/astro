package com.astro.app.feature.planning

import com.astro.app.core.planning.DayPlanning
import com.astro.app.core.planning.PlanningEntry
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

/** Une sauvegarde en retard ne doit jamais écraser une saisie plus récente. */
class PlanningAutosaveTest {

    private fun entries(text: String) = DayPlanning(listOf(PlanningEntry(0, text)))

    @Test
    fun `une ecriture plus ancienne terminee apres une plus recente est ignoree`() = runBlocking {
        val written = mutableListOf<String>()
        val autosave = PlanningAutosave { written += it.entries.single().text }
        val old = autosave.request(entries("ancien"))
        val recent = autosave.request(entries("récent"))
        autosave.persist(recent, entries("récent"))
        autosave.persist(old, entries("ancien"))
        assertEquals(listOf("récent"), written)
    }

    @Test
    fun `une ecriture lente ne laisse pas passer une plus ancienne apres elle`() = runBlocking {
        val written = mutableListOf<String>()
        val gate = CompletableDeferred<Unit>()
        val autosave = PlanningAutosave { list ->
            if (list.entries.single().text == "deux") gate.await()
            written += list.entries.single().text
        }
        val one = autosave.request(entries("un"))
        val two = autosave.request(entries("deux"))
        val slow = async { autosave.persist(two, entries("deux")) }
        yield()
        val late = async { autosave.persist(one, entries("un")) }
        gate.complete(Unit)
        slow.await()
        late.await()
        assertEquals(listOf("deux"), written)
    }

    @Test
    fun `flush ecrit le dernier etat demande une seule fois`() = runBlocking {
        val written = mutableListOf<String>()
        val autosave = PlanningAutosave { written += it.entries.single().text }
        autosave.request(entries("a"))
        autosave.request(entries("b"))
        autosave.flush()
        autosave.flush()
        assertEquals(listOf("b"), written)
    }

    @Test
    fun `flush sans demande n'ecrit rien`() = runBlocking {
        val written = mutableListOf<String>()
        PlanningAutosave { written += it.entries.single().text }.flush()
        assertEquals(emptyList<String>(), written)
    }
}
