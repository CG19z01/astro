package com.astro.app.core.alarm

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Ce que l'on peut dire du réveil d'une journée. */
sealed interface WakeAlarm {
    /** Le premier réveil de la journée sonne à [time]. */
    data class At(val time: LocalTime) : WakeAlarm

    /** Aucun réveil ne sonnera ce jour-là. */
    data object None : WakeAlarm

    /** Impossible à savoir : rien n'est affiché plutôt qu'une information inventée. */
    data object Unknown : WakeAlarm

    companion object {
        /**
         * Réveil de [date], connaissant seulement le prochain réveil de l'appareil [next].
         *
         * - le prochain réveil tombe ce jour-là : c'est le premier de la journée ;
         * - il tombe plus tard : aucun réveil ce jour-là,
         *   sans quoi il serait lui-même le prochain ;
         * - aucun réveil programmé : aucun non plus, pour les jours à venir ;
         * - [date] postérieure au prochain réveil : les réveils suivants ne sont pas
         *   connus d'Android, la réponse est donc inconnue ;
         * - [date] passée ou aujourd'hui : inconnue aussi, un réveil a pu sonner plus tôt
         *   dans la journée sans qu'Android le dise encore.
         */
        fun on(date: LocalDate, today: LocalDate, next: Instant?, zone: ZoneId): WakeAlarm {
            if (!date.isAfter(today)) return Unknown
            if (next == null) return None
            val ringing = next.atZone(zone)
            val day = ringing.toLocalDate()
            return when {
                day == date -> At(ringing.toLocalTime().withSecond(0).withNano(0))
                day.isAfter(date) -> None
                else -> Unknown
            }
        }
    }
}
