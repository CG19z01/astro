package com.astro.app.core.speech

/**
 * Découpe un texte trop long pour le moteur, qui refuse au-delà d'une longueur fixée
 * (`TextToSpeech.getMaxSpeechInputLength`, 4 000 caractères). Fonction pure, testée.
 *
 * On coupe de préférence après un paragraphe, sinon après une fin de phrase, sinon
 * après un espace : jamais au milieu d'un mot, sauf mot plus long que la limite.
 */
object SpeechChunks {

    private val SENTENCE_END = Regex("[.!?…;:]\\s")

    fun split(text: String, maxLength: Int): List<String> {
        require(maxLength > 0)
        val chunks = mutableListOf<String>()
        var rest = text.trim()
        while (rest.length > maxLength) {
            val window = rest.substring(0, maxLength)
            val cut = window.lastIndexOf("\n\n").takeIf { it > 0 }?.plus(2)
                ?: SENTENCE_END.findAll(window).lastOrNull()?.range?.last?.plus(1)
                ?: window.lastIndexOf(' ').takeIf { it > 0 }?.plus(1)
                ?: maxLength
            chunks += rest.substring(0, cut).trim()
            rest = rest.substring(cut).trim()
        }
        if (rest.isNotEmpty()) chunks += rest
        return chunks.filter { it.isNotEmpty() }
    }
}
