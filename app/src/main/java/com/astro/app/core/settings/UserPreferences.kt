package com.astro.app.core.settings

import java.time.ZoneId

/**
 * Préférences de l'utilisateur, hors activation des fonctionnalités.
 *
 * Regroupées dans un seul objet plutôt qu'en accesseurs séparés : ajouter un réglage
 * n'oblige alors ni à élargir l'interface du dépôt, ni à modifier les écrans existants.
 *
 * [latitude] et [longitude] sont nulles tant que l'utilisateur n'a rien renseigné.
 * Aucune position par défaut n'est inventée : sans coordonnées, les horaires du Soleil
 * et de la Lune ne sont pas affichés, plutôt que d'être calculés pour un lieu arbitraire.
 */
data class UserPreferences(
    /** Affichage de la barre de navigation basse. */
    val showNavigationBar: Boolean = true,
    /** Fuseau utilisé pour AFFICHER les horaires. Il n'intervient pas dans leur calcul. */
    val zoneId: ZoneId = ZoneId.systemDefault(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    /** Couleur des planètes-jours. */
    val planetColors: PlanetColors = PlanetColors(),
) {
    /** `true` si une position exploitable est renseignée. */
    val hasLocation: Boolean get() = latitude != null && longitude != null

    companion object {
        /**
         * Ne retient une latitude que si elle est physiquement possible.
         * Une valeur hors bornes ferait produire au calcul des horaires absurdes
         * plutôt qu'une erreur visible.
         */
        fun validLatitude(value: Double?): Double? = value?.takeIf { it in -90.0..90.0 }

        fun validLongitude(value: Double?): Double? = value?.takeIf { it in -180.0..180.0 }
    }
}
