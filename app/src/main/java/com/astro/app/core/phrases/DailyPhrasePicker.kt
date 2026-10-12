package com.astro.app.core.phrases

import java.time.LocalDate

/**
 * Choix de la phrase d'une journée. Fonction pure, testée.
 *
 * - Une journée déjà inscrite au journal garde SA phrase, même si la liste a changé
 *   depuis : l'historique ne se réécrit pas.
 * - Sinon, on choisit parmi les phrases les moins souvent montrées : toutes passent
 *   une fois avant qu'une revienne.
 * - Parmi elles, le rang dépend de la date ; le multiplicateur, premier, disperse
 *   les jours consécutifs dans la liste au lieu de la suivre dans l'ordre du fichier.
 */
object DailyPhrasePicker {

    private const val SPREAD = 7919L

    fun pick(date: LocalDate, phrases: List<Phrase>, journal: Map<LocalDate, Phrase>): Phrase? {
        journal[date]?.let { return it }
        if (phrases.isEmpty()) return null
        val shown = journal.values.groupingBy { it.text }.eachCount()
        val fewest = phrases.minOf { shown[it.text] ?: 0 }
        val candidates = phrases.filter { (shown[it.text] ?: 0) == fewest }
        val rank = Math.floorMod(date.toEpochDay() * SPREAD, candidates.size.toLong())
        return candidates[rank.toInt()]
    }
}
