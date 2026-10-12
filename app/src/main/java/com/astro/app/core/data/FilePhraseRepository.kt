package com.astro.app.core.data

import com.astro.app.core.phrases.Phrase
import com.astro.app.core.phrases.PhraseRepository
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

/** Une ligne du journal : la date en ISO 8601, stable quelle que soit la langue. */
@Serializable
private data class JournalLine(
    @SerialName("date") val date: String,
    @SerialName("phrase") val phrase: Phrase,
)

/**
 * Phrases et journal, dans deux fichiers JSON du stockage privé de l'application.
 *
 * Le fichier de l'utilisateur n'est pas recopié tel quel : seules les phrases lues et
 * validées sont enregistrées, dans un format maîtrisé par l'application.
 */
class FilePhraseRepository(
    directory: File,
    io: CoroutineDispatcher = Dispatchers.IO,
) : PhraseRepository {

    private val phraseStore = JsonFileStore(
        File(directory, "phrases.json"), ListSerializer(Phrase.serializer()), emptyList(), io,
    )
    private val journalStore = JsonFileStore(
        File(directory, "journal.json"), ListSerializer(JournalLine.serializer()), emptyList(), io,
    )

    override val phrases: Flow<List<Phrase>> = phraseStore.data

    override val journal: Flow<Map<LocalDate, Phrase>> = journalStore.data.map { lines ->
        lines.mapNotNull { line ->
            runCatching { LocalDate.parse(line.date) }.getOrNull()?.let { it to line.phrase }
        }.toMap()
    }

    override suspend fun replacePhrases(phrases: List<Phrase>) {
        phraseStore.update { phrases }
    }

    override suspend fun record(date: LocalDate, phrase: Phrase) {
        val key = date.toString()
        journalStore.update { lines ->
            if (lines.any { it.date == key }) lines else lines + JournalLine(key, phrase)
        }
    }
}
