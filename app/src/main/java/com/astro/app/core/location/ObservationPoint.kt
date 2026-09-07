package com.astro.app.core.location

import com.astro.app.core.settings.UserPreferences

/**
 * Lieu retenu pour le calcul des éphémérides.
 *
 * [temporary] distingue une position obtenue de l'appareil, valable le temps de la
 * session, d'une position saisie et enregistrée. L'interface doit le dire : une valeur
 * qui disparaîtra à la fermeture ne peut pas être présentée comme un réglage.
 */
data class ObservationPoint(
    val latitude: Double,
    val longitude: Double,
    val temporary: Boolean,
)

/**
 * Résolution du lieu d'observation.
 *
 * La position de session l'emporte sur les coordonnées enregistrées : c'est la demande
 * la plus récente et la plus explicite de l'utilisateur. Fonction pure, testable.
 */
object ObservationPoints {

    fun resolve(session: GeoPoint?, preferences: UserPreferences): ObservationPoint? = when {
        session != null -> ObservationPoint(session.latitude, session.longitude, temporary = true)
        preferences.hasLocation ->
            ObservationPoint(preferences.latitude!!, preferences.longitude!!, temporary = false)
        else -> null
    }
}
