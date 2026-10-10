package com.astro.app.feature.planning

import androidx.lifecycle.SavedStateHandle
import com.astro.app.core.planning.PlanningCodec
import com.astro.app.core.planning.DayPlanning
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

@OptIn(ExperimentalCoroutinesApi::class)
/** Déplacement des lignes et retour au tri automatique. */
class PlanningViewModelOrderTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(storage: PlanningRepository) = PlanningViewModel(
        SavedStateHandle(mapOf(PLANNING_DATE_ARG to "2026-10-18")), storage, flushScope = scope,
    ).also { scope.runCurrent() }

@Test
    fun `deplacer une ligne passe la journee en ordre manuel et l'enregistre`() {
        val storage = MemoryPlanning(PlanningCodec.encode(listOf(
            com.astro.app.core.planning.PlanningEntry(0, "a", LocalTime.of(9, 0)),
            com.astro.app.core.planning.PlanningEntry(1, "b"),
        )))
        val model = viewModel(storage)
        model.onMoveLine(model.lines.value!![1].id, 0)
        scope.advanceTimeBy(1_000); scope.runCurrent()
        val day = PlanningCodec.decodeDay(storage.raw.value)
        assertEquals(listOf("b", "a"), day.entries.map { it.text })
        assertEquals(true, day.manualOrder)
        assertEquals(LocalTime.of(9, 0), day.entries[1].time)
    }

    @Test
    fun `revenir au tri automatique garde les notes et retire l'ordre manuel`() {
        val storage = MemoryPlanning(PlanningCodec.encodeDay(DayPlanning(
            listOf(com.astro.app.core.planning.PlanningEntry(0, "b"), com.astro.app.core.planning.PlanningEntry(1, "a")),
            manualOrder = true,
        )))
        val model = viewModel(storage)
        assertEquals(true, model.manualOrder.value)
        model.onResetOrder()
        scope.advanceTimeBy(1_000); scope.runCurrent()
        val day = PlanningCodec.decodeDay(storage.raw.value)
        assertEquals(false, day.manualOrder)
        assertEquals(listOf("b", "a"), day.entries.map { it.text })
    }

    @Test
    fun `la ligne vide finale ne se deplace pas`() {
        val model = viewModel(MemoryPlanning("a"))
        val last = model.lines.value!!.last()
        model.onMoveLine(last.id, 0)
        assertEquals(last, model.lines.value!!.last())
        assertEquals(false, model.manualOrder.value)
    }

}
