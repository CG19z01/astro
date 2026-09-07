package com.astro.app.core.location

import android.content.Context
import android.location.LocationManager
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import android.os.CancellationSignal
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

/** Délai au-delà duquel on renonce à obtenir un point. */
private const val TIMEOUT_MILLIS = 12_000L

/**
 * Fournisseurs interrogés, du plus économe au plus coûteux.
 *
 * Le fournisseur réseau suffit largement : une erreur de quelques kilomètres décale un
 * lever de soleil de quelques secondes. Le GPS n'est sollicité qu'à défaut.
 */
private val PROVIDERS = listOf(
    LocationManager.NETWORK_PROVIDER,
    LocationManager.GPS_PROVIDER,
    LocationManager.PASSIVE_PROVIDER,
)

/**
 * Lecture d'une position ponctuelle auprès de l'appareil.
 *
 * Passe par `LocationManagerCompat`, qui gère lui-même les différences entre versions
 * d'Android depuis l'API 26 — aucune bibliothèque supplémentaire n'est nécessaire, et
 * en particulier pas les services Google Play.
 *
 * La position obtenue n'est pas enregistrée ici : l'appelant décide, et le projet a
 * choisi de ne la garder qu'en mémoire.
 */
class DeviceLocationProvider(private val context: Context) {

    /**
     * Renvoie une position, ou `null` si aucune n'est obtenue dans le délai imparti.
     *
     * Les fournisseurs sont essayés **les uns après les autres jusqu'à obtenir un point**.
     * S'arrêter au premier disponible serait fragile : un appareil peut exposer un
     * fournisseur réseau qui ne répond rien alors que son GPS fonctionne.
     */
    suspend fun currentPoint(): GeoPoint? {
        val manager = context.getSystemService<LocationManager>() ?: return null

        return withTimeoutOrNull(TIMEOUT_MILLIS) {
            PROVIDERS
                .filter { LocationManagerCompat.hasProvider(manager, it) }
                .firstNotNullOfOrNull { provider -> requestPoint(manager, provider) }
        }
    }

    private suspend fun requestPoint(manager: LocationManager, provider: String): GeoPoint? =
        suspendCancellableCoroutine { continuation ->
            val signal = CancellationSignal()
            continuation.invokeOnCancellation { signal.cancel() }
            val executor = Executor { command -> command.run() }

            runCatching {
                LocationManagerCompat.getCurrentLocation(manager, provider, signal, executor) { location ->
                    val point = location?.let { GeoPoint(it.latitude, it.longitude) }
                    if (continuation.isActive) continuation.resume(point)
                }
            }.onFailure {
                // Autorisation retirée entre-temps, ou fournisseur devenu indisponible.
                if (continuation.isActive) continuation.resume(null)
            }
        }
}
