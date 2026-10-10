package com.astro.app.core.planning

import java.time.LocalTime

/**
 * Une ligne du planning d'une journée : un texte d'une ligne, et une heure facultative.
 *
 * [id] identifie la ligne pendant l'édition. C'est lui, et non la position, qui relie
 * une heure à son texte : supprimer ou modifier une ligne ne déplace donc jamais
 * l'heure d'une autre. Il n'est pas enregistré — l'ordre des lignes suffit à les
 * relire, et chaque chargement attribue de nouveaux identifiants.
 */
data class PlanningEntry(
    val id: Long,
    val text: String,
    val time: LocalTime? = null,
) {
    /** Une ligne sans texte n'est pas une entrée, même si une heure lui est associée. */
    val isValid: Boolean get() = text.isNotBlank()
}
