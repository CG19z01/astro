package com.astro.app.core.phrases

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** À qui une phrase s'adresse, grammaticalement : « fier » ou « fière ». */
@Serializable
enum class PhraseGender {
    @SerialName("neutre") NEUTRAL,
    @SerialName("masculin") MASCULINE,
    @SerialName("féminin") FEMININE,
}

/**
 * Une phrase choisie par l'utilisateur. Aucune n'est fournie par l'application.
 *
 * Les noms JSON sont ceux du fichier que l'utilisateur écrit lui-même, en français :
 * « texte », « auteur », « theme », « genre ».
 */
@Serializable
data class Phrase(
    @SerialName("texte") val text: String,
    /** Vide si l'auteur n'est pas connu. */
    @SerialName("auteur") val author: String = "",
    /** Thème libre (« Confiance en soi et courage »…) ; vide s'il n'est pas précisé. */
    @SerialName("theme") val theme: String = "",
    @SerialName("genre") val gender: PhraseGender = PhraseGender.NEUTRAL,
)
