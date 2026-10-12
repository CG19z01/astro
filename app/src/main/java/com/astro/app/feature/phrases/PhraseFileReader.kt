package com.astro.app.feature.phrases

import android.content.ContentResolver
import android.net.Uri
import com.astro.app.core.config.PhrasesConfig
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Contenu d'un fichier choisi par l'utilisateur. */
sealed interface FileRead {
    data class Text(val content: String) : FileRead
    data object TooLarge : FileRead
    data object Unreadable : FileRead
}

/**
 * Lit le fichier choisi dans le sélecteur de fichiers d'Android.
 *
 * Aucune permission de stockage : le sélecteur n'accorde l'accès qu'au fichier choisi,
 * et seulement le temps de la lecture.
 */
class PhraseFileReader(private val resolver: ContentResolver) {

    suspend fun read(uri: Uri): FileRead = withContext(Dispatchers.IO) {
        try {
            val stream = resolver.openInputStream(uri) ?: return@withContext FileRead.Unreadable
            val bytes = stream.use { it.readNBytesCompat(PhrasesConfig.MAX_FILE_BYTES + 1) }
            if (bytes.size > PhrasesConfig.MAX_FILE_BYTES) FileRead.TooLarge
            else FileRead.Text(bytes.toString(Charsets.UTF_8).removePrefix("﻿"))
        } catch (cause: IOException) {
            FileRead.Unreadable
        } catch (cause: SecurityException) {
            FileRead.Unreadable
        }
    }

    /** `readNBytes` n'existe qu'à partir de l'API 33 côté Android. */
    private fun java.io.InputStream.readNBytesCompat(limit: Int): ByteArray {
        val buffer = java.io.ByteArrayOutputStream()
        val chunk = ByteArray(8192)
        while (buffer.size() < limit) {
            val read = read(chunk, 0, minOf(chunk.size, limit - buffer.size()))
            if (read < 0) break
            buffer.write(chunk, 0, read)
        }
        return buffer.toByteArray()
    }
}
