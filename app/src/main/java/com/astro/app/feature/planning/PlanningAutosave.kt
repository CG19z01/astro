package com.astro.app.feature.planning

import com.astro.app.core.planning.DayPlanning
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Écritures du planning, dans l'ordre de la saisie, quoi qu'il arrive.
 *
 * Chaque état à enregistrer reçoit un numéro de révision croissant. Une écriture ne
 * s'applique que si sa révision est plus récente que la dernière écrite : une
 * sauvegarde partie en retard ne peut donc jamais écraser une saisie plus récente,
 * même si elle termine après elle. Le verrou sérialise les écritures elles-mêmes.
 *
 * Indépendant de Compose et d'Android : testable en test unitaire JVM.
 */
class PlanningAutosave(
    private val write: suspend (DayPlanning) -> Unit,
) {
    private val lock = Mutex()
    private var lastWritten = 0L
    private var lastRequested = 0L
    private var pending: DayPlanning? = null

    /** Note un nouvel état à enregistrer ; renvoie sa révision. */
    @Synchronized
    fun request(day: DayPlanning): Long {
        lastRequested += 1
        pending = day
        return lastRequested
    }

    /** Écrit l'état de [revision] s'il est toujours le plus récent à écrire. */
    suspend fun persist(revision: Long, day: DayPlanning) {
        lock.withLock {
            if (revision <= lastWritten) return
            write(day)
            lastWritten = revision
        }
    }

    /** Écrit le dernier état demandé s'il ne l'a pas encore été. */
    suspend fun flush() {
        val (revision, entries) = synchronized(this) { lastRequested to pending }
        if (entries != null) persist(revision, entries)
    }
}
