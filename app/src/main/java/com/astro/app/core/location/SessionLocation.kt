package com.astro.app.core.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Une position, en degrés décimaux. */
data class GeoPoint(val latitude: Double, val longitude: Double)

/**
 * Position obtenue de l'appareil, gardée **en mémoire seulement**.
 *
 * Elle n'est jamais écrite sur le disque : ni dans les préférences, ni ailleurs. Elle
 * disparaît à la fermeture de l'application. C'est un choix délibéré — l'utilisateur
 * accorde l'accès à sa position pour la consultation en cours, pas pour toujours.
 *
 * Portée applicative et non liée à un écran : la Lune et le Soleil doivent voir la même
 * position dans la même session.
 */
class SessionLocation {

    private val _point = MutableStateFlow<GeoPoint?>(null)
    val point: StateFlow<GeoPoint?> = _point.asStateFlow()

    fun set(point: GeoPoint) {
        _point.value = point
    }

    fun clear() {
        _point.value = null
    }
}
