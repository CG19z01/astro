package com.astro.app.core.manifestation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Une manifestation écrite par l'utilisateur : un titre, un sujet libre et un texte.
 *
 * [id] est attribué à l'enregistrement ; 0 désigne une manifestation pas encore
 * enregistrée.
 */
@Serializable
data class Manifestation(
    @SerialName("id") val id: Long = 0,
    @SerialName("titre") val title: String,
    @SerialName("sujet") val subject: String,
    @SerialName("texte") val text: String,
) {
    /** Rien à enregistrer : ni titre ni texte. Un sujet seul ne fait pas une manifestation. */
    val isBlank: Boolean get() = title.isBlank() && text.isBlank()
}
