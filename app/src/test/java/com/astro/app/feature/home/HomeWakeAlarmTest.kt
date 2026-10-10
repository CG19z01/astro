package com.astro.app.feature.home

import com.astro.app.core.alarm.NextAlarmSource
import com.astro.app.core.config.FeatureId
import com.astro.app.core.alarm.WakeAlarm
import com.astro.app.core.planning.DayPlanning
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.time.SwipeDirection
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Le réveil affiché sur la planète J+1 suit le curseur, le réglage et Android. */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeWakeAlarmTest {

    private val zone = ZoneId.of("Europe/Paris")
    private val today = LocalDate.of(2026, 10, 10)
    private val alarm = MutableStateFlow(
        ZonedDateTime.of(today.plusDays(1), LocalTime.of(7, 15), zone).toInstant(),
    )
    private var shown = true

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = HomeViewModel(
        planning = object : PlanningRepository {
            override fun entryFor(date: LocalDate): Flow<String> = flowOf("")
            override fun allEntries(): Flow<Map<LocalDate, String>> = flowOf(emptyMap())
            override fun dayFor(date: LocalDate): Flow<DayPlanning> = flowOf(DayPlanning())
            override suspend fun saveDay(date: LocalDate, day: DayPlanning) = Unit
            override suspend fun save(date: LocalDate, text: String) = Unit
        },
        settings = object : SettingsRepository {
            override val featureStates = flowOf(emptyMap<FeatureId, Boolean>())
            override suspend fun setFeatureEnabled(id: FeatureId, enabled: Boolean) = Unit
            override val preferences = flowOf(UserPreferences(showNextAlarm = shown))
            override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) = Unit
        },
        today = { today },
        nextAlarm = NextAlarmSource { alarm },
        zone = { zone },
    )

    @Test
    fun `le reveil du lendemain apparait sur J+1`() = runTest {
        val model = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.wakeAlarm.collect {} }
        assertEquals(WakeAlarm.At(LocalTime.of(7, 15)), model.wakeAlarm.value)
    }

    @Test
    fun `un reveil coupe dans l'Horloge disparait`() = runTest {
        val model = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.wakeAlarm.collect {} }
        alarm.value = ZonedDateTime.of(today.plusDays(4), LocalTime.of(8, 0), zone).toInstant()
        assertEquals(WakeAlarm.None, model.wakeAlarm.value)
    }

    @Test
    fun `apres un swipe J+1 tombe apres le reveil connu, donc inconnu`() = runTest {
        val model = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.wakeAlarm.collect {} }
        model.onSwipe(SwipeDirection.FORWARD)
        assertEquals(WakeAlarm.Unknown, model.wakeAlarm.value)
    }

    @Test
    fun `reglage coupe rien n'est affiche`() = runTest {
        shown = false
        val model = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.wakeAlarm.collect {} }
        assertEquals(WakeAlarm.Unknown, model.wakeAlarm.value)
    }
}
