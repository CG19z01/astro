package com.astro.app.core.alarm

import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * Prochain réveil programmé sur l'appareil, ou `null` s'il n'y en a aucun.
 *
 * Interface : l'accueil se teste sans Android, avec une source fictive.
 */
fun interface NextAlarmSource {
    fun nextAlarm(): Flow<Instant?>
}
