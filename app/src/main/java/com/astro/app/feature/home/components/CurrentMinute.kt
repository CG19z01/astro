package com.astro.app.feature.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay

/**
 * L'instant présent, tronqué à la minute et mis à jour au changement de minute.
 *
 * Un rendez-vous de 10 h passe derrière les messages à venir à 10 h 01, sans qu'il faille
 * quitter puis rouvrir l'accueil. Une seule recomposition par minute : pas davantage.
 */
@Composable
fun rememberCurrentMinute(): State<LocalDateTime> =
    produceState(initialValue = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) {
        while (true) {
            val now = LocalDateTime.now()
            val next = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
            delay(ChronoUnit.MILLIS.between(now, next).coerceAtLeast(1L))
            value = next
        }
    }
