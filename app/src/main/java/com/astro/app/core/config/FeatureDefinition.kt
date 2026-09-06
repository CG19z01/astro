package com.astro.app.core.config

import androidx.annotation.StringRes

/**
 * Description déclarative d'une fonctionnalité.
 *
 * Aucun libellé n'est écrit ici : [titleRes] pointe vers `res/values/strings.xml`,
 * ce qui permet de changer les textes et de traduire l'application sans toucher au code.
 */
data class FeatureDefinition(
    val id: FeatureId,
    /** Route de navigation. Doit être unique dans le catalogue. */
    val route: String,
    @param:StringRes val titleRes: Int,
    /** État à la première installation, avant tout réglage de l'utilisateur. */
    val defaultEnabled: Boolean,
    /** `false` pour une fonctionnalité structurelle que l'utilisateur ne peut pas couper. */
    val canBeDisabled: Boolean,
    /** Présence dans la barre de navigation basse (voir la maquette : 5 boutons). */
    val inBottomBar: Boolean,
    /**
     * Univers visuel de l'écran. Déclaré ici et non dans l'écran lui-même, pour que
     * l'ambiance d'une page se lise au même endroit que le reste de sa définition.
     */
    val ambiance: Ambiance,
)
