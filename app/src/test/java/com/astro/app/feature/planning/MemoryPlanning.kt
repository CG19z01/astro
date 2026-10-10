package com.astro.app.feature.planning

import com.astro.app.core.planning.DayPlanning
import com.astro.app.core.planning.PlanningCodec
import com.astro.app.core.planning.PlanningRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** Stockage en mémoire, au format réel : on y lit ce qui serait écrit sur le disque. */
internal class MemoryPlanning(initial: String) : PlanningRepository {
    val raw = MutableStateFlow(initial)
    var writes = 0
    override fun entryFor(date: LocalDate): Flow<String> = entriesFor(date).map(PlanningCodec::display)
    override fun dayFor(date: LocalDate) = raw.map(PlanningCodec::decodeDay)
    override fun allEntries(): Flow<Map<LocalDate, String>> = raw.map { emptyMap() }
    override suspend fun save(date: LocalDate, text: String) = saveEntries(date, PlanningCodec.decode(text))
    override suspend fun saveDay(date: LocalDate, day: DayPlanning) {
        writes++
        raw.value = PlanningCodec.encodeDay(day)
    }
}
