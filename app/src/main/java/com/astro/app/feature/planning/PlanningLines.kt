package com.astro.app.feature.planning

import com.astro.app.core.planning.PlanningCodec
import com.astro.app.core.planning.PlanningEntry
import java.time.LocalTime

/**
 * Règles d'édition des lignes du planning. Fonctions pures, sans Compose ni Android.
 *
 * **Toujours une, et une seule, ligne vide à la fin.** Dès que la dernière ligne reçoit
 * du texte, une nouvelle ligne vide la suit. Une heure seule ne suffit pas : choisir
 * une heure sur la ligne vide ne crée rien. Si deux lignes sans texte se suivent en
 * fin de liste, la dernière est retirée — pourvu qu'elle n'ait pas d'heure, celle-ci
 * étant une saisie de l'utilisateur.
 *
 * **Lignes vides intermédiaires.** Une ligne sans texte ni heure, au milieu, est retirée
 * quand on la quitte ([dropIfEmpty]) — jamais pendant qu'on la modifie, ce qui ferait
 * disparaître la ligne sous les doigts.
 *
 * Chaque ligne garde son identifiant : modifier ou retirer une ligne ne touche jamais
 * l'heure d'une autre. L'ordre de saisie est conservé, aucun tri par heure.
 */
object PlanningLines {

    /** Lignes d'édition d'un planning enregistré, ligne vide finale comprise. */
    fun fromStored(entries: List<PlanningEntry>): List<PlanningEntry> = normalized(entries)

    fun withText(lines: List<PlanningEntry>, id: Long, text: String): List<PlanningEntry> =
        normalized(lines.map { if (it.id == id) it.copy(text = PlanningCodec.singleLine(text)) else it })

    fun withTime(lines: List<PlanningEntry>, id: Long, time: LocalTime?): List<PlanningEntry> =
        normalized(lines.map { if (it.id == id) it.copy(time = time) else it })

    /** Retire une ligne intermédiaire sans texte ni heure. La ligne finale reste. */
    fun dropIfEmpty(lines: List<PlanningEntry>, id: Long): List<PlanningEntry> {
        val index = lines.indexOfFirst { it.id == id }
        if (index < 0 || index == lines.lastIndex) return lines
        val line = lines[index]
        return if (line.text.isBlank() && line.time == null) normalized(lines - line) else lines
    }

    /**
     * Déplace une ligne à [toIndex]. La ligne vide finale ne se déplace pas et reste la
     * dernière : on ne peut ni la saisir, ni poser une ligne après elle.
     */
    fun move(lines: List<PlanningEntry>, id: Long, toIndex: Int): List<PlanningEntry> {
        val from = lines.indexOfFirst { it.id == id }
        if (from < 0 || from == lines.lastIndex) return lines
        val target = toIndex.coerceIn(0, lines.lastIndex - 1)
        if (target == from) return lines
        val moved = lines.toMutableList()
        moved.add(target, moved.removeAt(from))
        return moved
    }

    /** Ce qui s'enregistre : les lignes qui ont du texte, ligne vide finale exclue. */
    fun toPersist(lines: List<PlanningEntry>): List<PlanningEntry> = lines.filter { it.isValid }

    private fun normalized(lines: List<PlanningEntry>): List<PlanningEntry> {
        val result = lines.toMutableList()
        while (result.size >= 2 && result[result.lastIndex - 1].text.isBlank() && result.last().let {
                it.text.isBlank() && it.time == null
            }
        ) {
            result.removeAt(result.lastIndex)
        }
        if (result.isEmpty() || result.last().text.isNotBlank()) {
            result += PlanningEntry(id = (result.maxOfOrNull { it.id } ?: -1L) + 1, text = "")
        }
        return result
    }
}
