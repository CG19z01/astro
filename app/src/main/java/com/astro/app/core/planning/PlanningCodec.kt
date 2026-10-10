package com.astro.app.core.planning

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Format d'enregistrement du planning d'une journée, et lecture des anciennes notes.
 *
 * **Format actuel.** Une ligne d'en-tête ([HEADER], ou [MANUAL_HEADER] si l'ordre a été
 * choisi à la main), puis une ligne par entrée : l'heure
 * sur cinq caractères (`09:30`, ou [NO_TIME]) suivie du texte. Comme pour les règles de
 * couleur, un préfixe de longueur fixe évite tout séparateur : aucun caractère du texte
 * ne peut casser la lecture. Le texte d'une entrée tient sur une ligne, les retours à
 * la ligne en sont retirés.
 *
 * **Anciennes notes.** Avant le 2026-10-10, une date portait un texte libre. Une valeur
 * sans en-tête est lue comme telle : chaque ligne non blanche devient une entrée sans
 * heure, dans l'ordre. Rien n'est réécrit à la lecture — une note n'est convertie que
 * lorsque l'utilisateur la modifie, et aucune n'est donc perdue sans intervention.
 *
 * L'en-tête commence par un caractère de contrôle (séparateur d'enregistrement) qu'un
 * clavier ne produit pas : une ancienne note ne peut pas être prise pour le nouveau
 * format.
 *
 * Objet pur, testable sans Android.
 */
object PlanningCodec {

    const val HEADER = "\u001Eastro-planning-2"

    /** Même format, journée dont l'ordre a été choisi à la main (2026-10-10). */
    const val MANUAL_HEADER = "\u001Eastro-planning-2-manuel"
    private const val NO_TIME = "--:--"
    private const val TIME_LENGTH = 5
    private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    /** Valeur à enregistrer. Les lignes sans texte ne sont pas des entrées : omises. */
    fun encode(entries: List<PlanningEntry>): String = encodeDay(DayPlanning(entries))

    /** Comme [encode], l'en-tête portant en plus le choix d'un ordre manuel. */
    fun encodeDay(day: DayPlanning): String {
        val valid = day.entries.filter { it.isValid }
        if (valid.isEmpty()) return ""
        val header = if (day.manualOrder) MANUAL_HEADER else HEADER
        return (listOf(header) + valid.map { entry ->
            val time = entry.time?.format(TIME_FORMAT) ?: NO_TIME
            time + singleLine(entry.text).trim()
        }).joinToString("\n")
    }

    /** Entrées d'une valeur enregistrée, dans l'ordre de saisie. */
    fun decode(stored: String): List<PlanningEntry> = decodeDay(stored).entries

    /** Entrées et choix d'ordre. Une ancienne note n'a jamais d'ordre manuel. */
    fun decodeDay(stored: String): DayPlanning {
        val lines = stored.lines()
        val manual = when (lines.firstOrNull()) {
            HEADER -> false
            MANUAL_HEADER -> true
            else -> return DayPlanning(decodeLegacy(stored))
        }
        return DayPlanning(entries = parse(lines.drop(1)), manualOrder = manual)
    }

    private fun parse(lines: List<String>): List<PlanningEntry> =
        lines.filter { it.length > TIME_LENGTH }.mapIndexed { index, line ->
            val time = line.take(TIME_LENGTH).takeUnless { it == NO_TIME }
                ?.let { runCatching { LocalTime.parse(it, TIME_FORMAT) }.getOrNull() }
            PlanningEntry(id = index.toLong(), text = line.drop(TIME_LENGTH), time = time)
        }

    /**
     * Texte lisible d'une journée : une entrée par ligne, l'heure devant quand elle
     * existe (« 09:30 Dîner »). C'est ce que lisent l'accueil, le Calendrier et les
     * règles de couleur par mot.
     */
    fun display(entries: List<PlanningEntry>): String =
        entries.filter { it.isValid }.joinToString("\n") { entry ->
            listOfNotNull(entry.time?.format(TIME_FORMAT), entry.text.trim()).joinToString(" ")
        }

    private fun decodeLegacy(text: String): List<PlanningEntry> =
        text.lines().filter { it.isNotBlank() }.mapIndexed { index, line ->
            PlanningEntry(id = index.toLong(), text = line.trim())
        }

    /** Retire les retours à la ligne : une entrée tient sur une ligne. */
    fun singleLine(text: String): String = text.replace("\r", "").replace('\n', ' ')
}
