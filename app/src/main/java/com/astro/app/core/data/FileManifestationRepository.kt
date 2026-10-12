package com.astro.app.core.data

import com.astro.app.core.manifestation.Manifestation
import com.astro.app.core.manifestation.ManifestationRepository
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer

/**
 * Manifestations dans un fichier JSON du stockage privé de l'application.
 *
 * Les identifiants croissent : une nouvelle manifestation reçoit le plus grand id
 * existant plus un. Ils servent donc aussi d'ordre de création, sans horloge.
 */
class FileManifestationRepository(
    directory: File,
    io: CoroutineDispatcher = Dispatchers.IO,
) : ManifestationRepository {

    private val store = JsonFileStore(
        File(directory, "manifestations.json"),
        ListSerializer(Manifestation.serializer()),
        emptyList(),
        io,
    )

    override val all: Flow<List<Manifestation>> = store.data.map { list -> list.sortedByDescending { it.id } }

    override suspend fun save(manifestation: Manifestation): Long {
        var saved = manifestation.id
        store.update { list ->
            if (list.any { it.id == manifestation.id }) {
                list.map { if (it.id == manifestation.id) manifestation else it }
            } else {
                saved = (list.maxOfOrNull { it.id } ?: 0) + 1
                list + manifestation.copy(id = saved)
            }
        }
        return saved
    }

    override suspend fun delete(id: Long) {
        store.update { list -> list.filterNot { it.id == id } }
    }
}
