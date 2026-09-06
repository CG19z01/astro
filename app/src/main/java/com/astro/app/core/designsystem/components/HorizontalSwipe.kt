package com.astro.app.core.designsystem.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.astro.app.core.config.TimeNavigationConfig
import com.astro.app.core.time.SwipeDirection

/**
 * Traduit les gestes horizontaux en swipes qualifiés.
 *
 * Un seul endroit décide de ce qu'est un swipe et dans quel sens il va, pour la Home
 * comme pour le Calendrier. Un glissement plus court que le seuil n'est pas compté.
 *
 * Convention retenue : glisser vers la droite va vers le PASSÉ, comme on retourne une
 * page en arrière ; glisser vers la gauche va vers l'avenir.
 */
fun Modifier.onHorizontalSwipe(key: Any?, onSwipe: (SwipeDirection) -> Unit): Modifier =
    pointerInput(key) {
        val threshold = TimeNavigationConfig.SWIPE_MIN_DISTANCE_DP.dp.toPx()
        var travelled = 0f

        detectHorizontalDragGestures(
            onDragStart = { travelled = 0f },
            onDragCancel = { travelled = 0f },
            onDragEnd = {
                when {
                    travelled <= -threshold -> onSwipe(SwipeDirection.FORWARD)
                    travelled >= threshold -> onSwipe(SwipeDirection.BACKWARD)
                }
            },
            onHorizontalDrag = { _, amount -> travelled += amount },
        )
    }
