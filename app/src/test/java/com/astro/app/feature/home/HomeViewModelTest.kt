package com.astro.app.feature.home

import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

/** Dépôt factice : ces tests portent sur le curseur, pas sur le stockage. */
private class NoPlanning : PlanningRepository {
    override fun entryFor(date: LocalDate): Flow<String> = flowOf("")
    override suspend fun save(date: LocalDate, text: String) = Unit
}

class HomeViewModelTest {

    private val today = LocalDate.of(2026, 9, 6)

    private fun viewModel() = HomeViewModel(NoPlanning()) { today }

    @Test
    fun `le curseur demarre sur la journee en cours`() {
        assertEquals(today, viewModel().cursor.value.anchor)
        assertEquals(TimeScale.DAY, viewModel().cursor.value.scale)
    }

    @Test
    fun `un swipe arriere recule d'une journee`() {
        val model = viewModel()
        model.onSwipe(SwipeDirection.BACKWARD)
        assertEquals(LocalDate.of(2026, 9, 5), model.cursor.value.anchor)
    }

    @Test
    fun `le maintien long ramene sur la journee en cours apres plusieurs swipes`() {
        val model = viewModel()
        repeat(5) { model.onSwipe(SwipeDirection.BACKWARD) }
        assertEquals(LocalDate.of(2026, 9, 1), model.cursor.value.anchor)

        model.resetToToday()
        assertEquals(today, model.cursor.value.anchor)
    }

    @Test
    fun `le retour a aujourd'hui ne change pas la temporalite`() {
        val model = viewModel()
        repeat(3) { model.onSwipe(SwipeDirection.FORWARD) }
        val scaleBefore = model.cursor.value.scale
        model.resetToToday()
        assertEquals(scaleBefore, model.cursor.value.scale)
    }

    @Test
    fun `le retour a aujourd'hui depuis aujourd'hui ne change rien`() {
        val model = viewModel()
        val before = model.cursor.value
        model.resetToToday()
        assertEquals(before, model.cursor.value)
    }
}
