package com.astro.app.feature.calendar

import com.astro.app.core.config.FeatureId
import com.astro.app.core.planning.DayPlanning
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class NoPlanning : PlanningRepository {
    override fun entryFor(date: LocalDate): Flow<String> = flowOf("")
    override fun allEntries(): Flow<Map<LocalDate, String>> = flowOf(emptyMap())
    override fun dayFor(date: LocalDate): Flow<DayPlanning> = flowOf(DayPlanning())
    override suspend fun saveDay(date: LocalDate, day: DayPlanning) = Unit
    override suspend fun save(date: LocalDate, text: String) = Unit
}

/** Préférences modifiables, pour vérifier ce que le Calendrier lit et n'écrit pas. */
private class FakeSettings(initial: UserPreferences) : SettingsRepository {
    val stored = MutableStateFlow(initial)
    var writes = 0
    override val featureStates: Flow<Map<FeatureId, Boolean>> = flowOf(emptyMap())
    override suspend fun setFeatureEnabled(id: FeatureId, enabled: Boolean) = Unit
    override val preferences: Flow<UserPreferences> = stored
    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        writes++
        stored.value = transform(stored.value)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val today = LocalDate.of(2026, 10, 9)

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(settings: SettingsRepository) = CalendarViewModel(NoPlanning(), settings) { today }

    @Test
    fun `sans preference enregistree, le calendrier s'ouvre en vue Mois sur aujourd'hui`() {
        val state = viewModel(FakeSettings(UserPreferences())).state.value!!
        assertEquals(TimeScale.MONTH, state.scale)
        assertEquals(today, state.selected)
    }

    @Test
    fun `le calendrier s'ouvre sur la vue choisie dans les Parametres`() {
        val settings = FakeSettings(UserPreferences(calendarInitialScale = TimeScale.YEAR))
        assertEquals(TimeScale.YEAR, viewModel(settings).state.value!!.scale)
    }

    @Test
    fun `changer de vue ne modifie pas la preference`() {
        val settings = FakeSettings(UserPreferences(calendarInitialScale = TimeScale.WEEK))
        val model = viewModel(settings)
        model.onSelectScale(TimeScale.DAY)
        listOf(0L, 200L, 400L).forEach { model.onSwipe(SwipeDirection.FORWARD, it) }
        assertEquals(0, settings.writes)
        assertEquals(TimeScale.WEEK, settings.stored.value.calendarInitialScale)
    }

    @Test
    fun `une nouvelle ouverture repart de la preference, pas de la derniere vue`() {
        val settings = FakeSettings(UserPreferences(calendarInitialScale = TimeScale.MONTH))
        viewModel(settings).onSelectScale(TimeScale.YEAR)
        assertEquals(TimeScale.MONTH, viewModel(settings).state.value!!.scale)
    }

    @Test
    fun `un premier appui selectionne, un second appui ouvre le planning`() {
        val model = viewModel(FakeSettings(UserPreferences()))
        val october18 = LocalDate.of(2026, 10, 18)
        assertFalse(model.onDayTapped(october18))
        assertEquals(october18, model.state.value!!.selected)
        assertTrue(model.onDayTapped(october18))
    }

    @Test
    fun `scenario de reference, du 18 octobre en vue Mois a sa semaine`() {
        val model = viewModel(FakeSettings(UserPreferences(calendarInitialScale = TimeScale.MONTH)))
        model.onDayTapped(LocalDate.of(2026, 10, 18))
        model.onSelectScale(TimeScale.WEEK)
        val state = model.state.value!!
        assertEquals(LocalDate.of(2026, 10, 12), state.firstDay)
        assertEquals(LocalDate.of(2026, 10, 18), state.lastDay)
    }
}
