package com.astro.app.core.planning

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Ordre d'affichage des messages d'une journée sur l'accueil.
 *
 *  1. les messages à venir, du plus tôt au plus tard ;
 *  2. les messages dont l'heure est passée, du plus tôt au plus tard — toujours là,
 *     accessibles en faisant défiler, mais plus en tête ;
 *  3. les messages sans heure, tout en bas, dans leur ordre de saisie.
 *
 * Une heure égale à l'instant présent est encore « à venir ». À heure égale, l'ordre
 * de saisie est conservé (tri stable). Rien n'est retiré ni modifié : seul l'ordre
 * change, et uniquement pour l'affichage — l'ordre enregistré reste celui de la saisie.
 *
 * Fonction pure : l'instant présent est fourni par l'appelant.
 */
object PlanningOrder {

    fun forDisplay(entries: List<PlanningEntry>, date: LocalDate, now: LocalDateTime): List<PlanningEntry> {
        val valid = entries.filter { it.isValid }
        val (timed, untimed) = valid.partition { it.time != null }
        val (upcoming, past) = timed
            .sortedBy { it.time }
            .partition { !LocalDateTime.of(date, it.time).isBefore(now) }
        return upcoming + past + untimed
    }
}
