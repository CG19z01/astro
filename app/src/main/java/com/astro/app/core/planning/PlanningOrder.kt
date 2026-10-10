package com.astro.app.core.planning

/**
 * Ordre d'affichage des messages d'une journée sur l'accueil.
 *
 * - **Ordre manuel** ([DayPlanning.manualOrder]) : celui que l'utilisateur a choisi en
 *   déplaçant les entrées, tel quel.
 * - **Sinon** : les messages à heure du plus tôt au plus tard, et les messages sans
 *   heure, dans leur ordre de saisie, en haut ou en bas selon le réglage
 *   [untimedFirst]. À heure égale, l'ordre de saisie est conservé (tri stable).
 *
 * Choix de l'utilisateur, 2026-10-10 : ordre strictement chronologique, sans faire
 * descendre les rendez-vous déjà passés (règle abandonnée le même jour).
 *
 * Rien n'est retiré ni modifié : l'ordre ne vaut que pour l'affichage. Fonction pure.
 */
object PlanningOrder {

    fun forDisplay(day: DayPlanning, untimedFirst: Boolean): List<PlanningEntry> {
        val valid = day.entries.filter { it.isValid }
        if (day.manualOrder) return valid
        val (timed, untimed) = valid.partition { it.time != null }
        val chronological = timed.sortedBy { it.time }
        return if (untimedFirst) untimed + chronological else chronological + untimed
    }
}
