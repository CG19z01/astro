package com.astro.app.core.phrases

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Une phrase choisie par l'utilisateur. Aucune n'est fournie par l'application.
 *
 * Les noms JSON sont ceux du fichier que l'utilisateur écrit lui-même : « texte » et
 * « auteur », en français.
 */
@Serializable
data class Phrase(
    @SerialName("texte") val text: String,
    /** Vide si l'auteur n'est pas connu. */
    @SerialName("auteur") val author: String = "",
)
