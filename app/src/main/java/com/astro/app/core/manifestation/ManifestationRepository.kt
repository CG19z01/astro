package com.astro.app.core.manifestation

import kotlinx.coroutines.flow.Flow

/** Manifestations de l'utilisateur, sur le téléphone uniquement. */
interface ManifestationRepository {
    /** Toutes, la plus récemment créée en premier. */
    val all: Flow<List<Manifestation>>

    /** Crée (id 0) ou remplace (id connu). Renvoie l'id enregistré. */
    suspend fun save(manifestation: Manifestation): Long

    suspend fun delete(id: Long)
}
