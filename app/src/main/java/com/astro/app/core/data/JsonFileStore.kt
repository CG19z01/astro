package com.astro.app.core.data

import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Une valeur enregistrée dans un fichier JSON du stockage privé de l'application.
 *
 * - Lu une seule fois, au premier abonnement, puis servi depuis la mémoire.
 * - Écriture **atomique** : dans un fichier voisin, puis renommage. Un arrêt brutal
 *   pendant l'écriture laisse l'ancien fichier intact, jamais un fichier tronqué.
 * - Un fichier illisible ne fait pas planter l'application : il est mis de côté
 *   (`.illisible`) plutôt qu'écrasé, et l'on repart de [empty].
 *
 * Indépendant d'Android : testable en test unitaire JVM, sur un dossier temporaire.
 */
class JsonFileStore<T>(
    private val file: File,
    private val serializer: KSerializer<T>,
    private val empty: T,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val lock = Mutex()
    private val state = MutableStateFlow<T?>(null)

    val data: Flow<T> = flow {
        load()
        emitAll(state.filterNotNull())
    }

    /** Remplace la valeur par `transform(valeur actuelle)` et l'écrit. */
    suspend fun update(transform: (T) -> T) {
        load()
        lock.withLock {
            val updated = transform(state.value ?: empty)
            withContext(io) { write(updated) }
            state.value = updated
        }
    }

    private suspend fun load() {
        if (state.value != null) return
        lock.withLock {
            if (state.value != null) return
            state.value = withContext(io) { read() }
        }
    }

    private fun read(): T {
        if (!file.exists()) return empty
        return try {
            json.decodeFromString(serializer, file.readText())
        } catch (cause: IOException) {
            empty
        } catch (cause: IllegalArgumentException) {
            // SerializationException en dérive : contenu présent mais illisible.
            file.renameTo(File(file.path + ".illisible"))
            empty
        }
    }

    private fun write(value: T) {
        file.parentFile?.mkdirs()
        val temporary = File(file.path + ".tmp")
        temporary.writeText(json.encodeToString(serializer, value))
        if (!temporary.renameTo(file)) {
            // Certains systèmes refusent de renommer sur un fichier existant.
            file.delete()
            if (!temporary.renameTo(file)) throw IOException("Écriture impossible : $file")
        }
    }
}
