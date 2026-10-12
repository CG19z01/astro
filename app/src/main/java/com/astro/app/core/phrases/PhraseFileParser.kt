package com.astro.app.core.phrases

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** Résultat de la lecture d'un fichier de phrases. */
sealed interface PhraseImport {
    data class Success(val phrases: List<Phrase>) : PhraseImport

    /** [position] : rang de la phrase fautive, à partir de 1 ; nul si le défaut est global. */
    data class Failure(val reason: Reason, val position: Int? = null) : PhraseImport

    enum class Reason { NOT_JSON, NOT_A_LIST, EMPTY, MISSING_TEXT }
}

/**
 * Lit le fichier JSON de phrases écrit par l'utilisateur.
 *
 * Format attendu : une liste. Chaque élément est soit un objet
 * `{ "texte": "…", "auteur": "…" }` (auteur facultatif), soit directement une chaîne.
 * Les espaces en bord sont retirés, les doublons exacts ignorés.
 *
 * Tout ou rien : une seule phrase sans texte fait refuser le fichier, en indiquant
 * laquelle. Importer une partie seulement laisserait croire que tout est passé.
 */
object PhraseFileParser {

    fun parse(content: String): PhraseImport {
        val root = runCatching { Json.parseToJsonElement(content) }.getOrNull()
            ?: return PhraseImport.Failure(PhraseImport.Reason.NOT_JSON)
        val items = root as? JsonArray ?: return PhraseImport.Failure(PhraseImport.Reason.NOT_A_LIST)
        if (items.isEmpty()) return PhraseImport.Failure(PhraseImport.Reason.EMPTY)

        val phrases = items.mapIndexed { index, item ->
            phraseOf(item) ?: return PhraseImport.Failure(PhraseImport.Reason.MISSING_TEXT, index + 1)
        }
        return PhraseImport.Success(phrases.distinct())
    }

    private fun phraseOf(item: kotlinx.serialization.json.JsonElement): Phrase? {
        val (text, author) = when (item) {
            is JsonPrimitive -> (if (item.isString) item.content else null) to null
            is JsonObject -> item.string("texte") to item.string("auteur")
            else -> null to null
        }
        val cleaned = text?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return Phrase(cleaned, author?.trim().orEmpty())
    }

    private fun JsonObject.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.contentOrNull
}
