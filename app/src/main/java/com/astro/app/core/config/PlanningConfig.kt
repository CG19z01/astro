package com.astro.app.core.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Réglages de la page Planning. */
object PlanningConfig {

    /**
     * Pause de frappe avant l'enregistrement automatique, en millisecondes : ni bouton,
     * ni écriture disque à chaque touche.
     */
    const val AUTOSAVE_DELAY_MILLIS: Long = 600L

    /** Les notes sans heure s'affichent en bas tant que l'utilisateur n'a rien choisi. */
    const val DEFAULT_UNTIMED_FIRST: Boolean = false

    /** Heures proposées par la roue : de 00 à 23, de 1 en 1. */
    val HOURS: List<Int> = (0..23).toList()

    /** Minutes proposées par la roue : de 00 à 55, de 5 en 5. */
    const val MINUTE_STEP: Int = 5
    val MINUTES: List<Int> = (0 until 60 step MINUTE_STEP).toList()

    /**
     * Largeur en dessous de laquelle l'heure passe sous le texte au lieu d'être à côté :
     * le champ garderait sinon trop peu de place pour être lisible.
     */
    val INLINE_TIME_MIN_WIDTH: Dp = 300.dp

    /**
     * Largeur fixe de la pastille d'heure : « Heure » et « 14:20 » n'ont pas la même
     * largeur, et les champs se décalaient d'une ligne à l'autre (constaté sur émulateur).
     */
    val TIME_CHIP_WIDTH: Dp = 80.dp

    /** Grossissement d'une note saisie pour être déplacée : elle « se soulève ». */
    const val DRAG_LIFT_SCALE: Float = 1.03f

    /** Ombre portée de la note soulevée. */
    val DRAG_ELEVATION: Dp = 10.dp

    /** Valeurs visibles à la fois sur une roue : celle du centre, et une de part et d'autre. */
    const val WHEEL_VISIBLE_ITEMS: Int = 3

    /** Hauteur d'une valeur sur la roue. */
    val WHEEL_ITEM_HEIGHT: Dp = 44.dp

    /** Largeur d'une roue (heures ou minutes). */
    val WHEEL_WIDTH: Dp = 72.dp
}
