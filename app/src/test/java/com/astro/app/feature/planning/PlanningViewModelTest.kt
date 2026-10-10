package com.astro.app.feature.planning

import androidx.lifecycle.SavedStateHandle
import com.astro.app.core.planning.PlanningCodec
import com.astro.app.core.planning.PlanningEntry
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.navigation.PLANNING_DATE_ARG
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Stockage en mémoire, au format réel : on y lit ce qui serait écrit sur le disque. */
private class MemoryPlanning(initial: String) : PlanningRepository {
    val raw = MutableStateFlow(initial)
    var writes = 0
    override fun entryFor(date: LocalDate): Flow<String> = entriesFor(date).map(PlanningCodec::display)
    override fun entriesFor(date: LocalDate) = raw.map(PlanningCodec::decode)
    override fun allEntries(): Flow<Map<LocalDate, String>> = raw.map { emptyMap() }
    override suspend fun save(date: LocalDate, text: String) = saveEntries(date, PlanningCodec.decode(text))
    override suspend fun saveEntries(date: LocalDate, entries: List<PlanningEntry>) {
        writes++
        raw.value = PlanningCodec.encode(entries)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlanningViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(storage: PlanningRepository) = PlanningViewModel(
        SavedStateHandle(mapOf(PLANNING_DATE_ARG to "2026-10-18")), storage, flushScope = scope,
    ).also { scope.runCurrent() }

    @Test
    fun `une ancienne note s'ouvre en lignes, suivie d'une ligne vide`() {
        val model = viewModel(MemoryPlanning("Diner chez Marie"))
        assertEquals(listOf("Diner chez Marie", ""), model.lines.value!!.map { it.text })
    }

    @Test
    fun `la saisie est enregistree apres la pause, pas avant`() {
        val storage = MemoryPlanning("")
        val model = viewModel(storage)
        model.onTextChange(model.lines.value!!.last().id, "Sport")
        scope.advanceTimeBy(500); scope.runCurrent()
        assertEquals(0, storage.writes)
        scope.advanceTimeBy(200); scope.runCurrent()
        assertEquals(listOf("Sport"), PlanningCodec.decode(storage.raw.value).map { it.text })
    }

    @Test
    fun `une frappe continue ne provoque qu'une ecriture`() {
        val storage = MemoryPlanning("")
        val model = viewModel(storage)
        val id = model.lines.value!!.last().id
        "Sport".indices.forEach { model.onTextChange(id, "Sport".take(it + 1)); scope.advanceTimeBy(100) }
        scope.advanceTimeBy(1_000); scope.runCurrent()
        assertEquals(1, storage.writes)
    }

    @Test
    fun `une heure seule sur la ligne vide n'ecrit rien`() {
        val storage = MemoryPlanning("")
        val model = viewModel(storage)
        model.onTimeChange(model.lines.value!!.last().id, LocalTime.of(9, 0))
        scope.advanceTimeBy(1_000); scope.runCurrent()
        assertEquals(0, storage.writes)
    }

    @Test
    fun `quitter avant la fin de la pause enregistre quand meme`() {
        val storage = MemoryPlanning("")
        val model = viewModel(storage)
        model.onTextChange(model.lines.value!!.last().id, "Sport")
        model.javaClass.getDeclaredMethod("onCleared").apply { isAccessible = true }.invoke(model)
        scope.runCurrent()
        assertEquals(listOf("Sport"), PlanningCodec.decode(storage.raw.value).map { it.text })
    }

    @Test
    fun `l'heure choisie est enregistree avec son texte`() {
        val storage = MemoryPlanning("Dîner")
        val model = viewModel(storage)
        model.onTimeChange(model.lines.value!!.first().id, LocalTime.of(20, 30))
        scope.advanceTimeBy(1_000); scope.runCurrent()
        assertEquals("20:30 Dîner", PlanningCodec.display(PlanningCodec.decode(storage.raw.value)))
    }

    @Test
    fun `tout effacer supprime le planning`() {
        val storage = MemoryPlanning("Dîner")
        val model = viewModel(storage)
        model.onTextChange(model.lines.value!!.first().id, "")
        scope.advanceTimeBy(1_000); scope.runCurrent()
        assertEquals("", storage.raw.value)
    }
}
