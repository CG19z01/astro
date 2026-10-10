package com.astro.app.feature.planning

/**
 * Où tombe une ligne que l'on fait glisser. Fonction pure, sans Compose.
 *
 * La ligne change de place dès qu'elle a dépassé la MOITIÉ de sa voisine : c'est le
 * seuil qui donne l'impression de briques qui s'échangent, ni trop tôt ni trop tard.
 * Les hauteurs sont mesurées, les lignes n'ayant pas toutes la même (disposition
 * étroite, texte agrandi par l'accessibilité).
 *
 * [maxIndex] borne la descente : la ligne vide finale reste toujours la dernière.
 */
object DragOrder {

    fun targetIndex(heights: List<Int>, from: Int, offset: Float, spacing: Float, maxIndex: Int): Int {
        var index = from
        var remaining = offset
        while (remaining > 0 && index < maxIndex) {
            val next = heights[index + 1] + spacing
            if (remaining <= next / 2) break
            remaining -= next
            index++
        }
        while (remaining < 0 && index > 0) {
            val previous = heights[index - 1] + spacing
            if (-remaining <= previous / 2) break
            remaining += previous
            index--
        }
        return index
    }

    /**
     * Décalage d'une ligne non saisie pendant le glissement : elle cède sa place d'une
     * hauteur de la ligne saisie, vers le haut ou vers le bas, si celle-ci l'a franchie.
     */
    fun shiftOf(index: Int, from: Int, target: Int, draggedHeight: Float): Float = when {
        index == from -> 0f
        index in (from + 1)..target -> -draggedHeight
        index in target until from -> draggedHeight
        else -> 0f
    }
}
