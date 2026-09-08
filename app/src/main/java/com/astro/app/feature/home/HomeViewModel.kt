package com.astro.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.config.HomeLayoutConfig
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * État de la page d'accueil : où l'on se trouve dans le temps, et ce que contient le
 * planning de la période affichée.
 *
 * Le curseur vit dans le ViewModel et non dans la composition : il survit ainsi à une
 * rotation de l'écran et à un aller-retour vers une autre page, ce qui est le
 * comportement attendu d'une navigation temporelle.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val planning: PlanningRepository,
    settings: SettingsRepository,
    /**
     * Source de la date du jour. Injectée plutôt que lue directement : sans cela, le
     * retour à aujourd'hui dépendrait de l'horloge système et ne serait pas testable.
     */
    private val today: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val _cursor = MutableStateFlow(TimeCursor.of(TimeScale.INITIAL, today()))
    val cursor: StateFlow<TimeCursor> = _cursor.asStateFlow()

    /** Planning de la période courante, suivi en continu : une écriture se voit aussitôt. */
    val planningPreview: StateFlow<String> = _cursor
        .flatMapLatest { current -> planning.entryFor(current.anchor) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), "")

    /**
     * Planning de la période suivante. Nécessaire à la couleur de la seconde bille :
     * une règle de mot doit pouvoir la teindre elle aussi.
     */
    val nextPlanningPreview: StateFlow<String> = _cursor
        .flatMapLatest { current -> planning.entryFor(current.next().anchor) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), "")

    /**
     * Plannings des journées portées par les planètes d'accompagnement, dans l'ordre
     * de `HomeLayoutConfig.COMPANION_OFFSETS`. Nécessaires à leur titre et à leur
     * couleur, une règle de mot pouvant les teindre elles aussi.
     */
    val companionPlannings: StateFlow<List<String>> = _cursor
        .flatMapLatest { current ->
            combine(HomeLayoutConfig.COMPANION_OFFSETS.map { offset ->
                planning.entryFor(current.shifted(offset).anchor)
            }) { entries -> entries.toList() }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            List(HomeLayoutConfig.COMPANION_OFFSETS.size) { "" },
        )

    /** Réglages de couleur des planètes-jours. */
    val planetColors: StateFlow<PlanetColors> = settings.preferences
        .map { it.planetColors }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PlanetColors())

    /**
     * Déplace le curseur DANS la temporalité courante.
     * Ne change jamais de temporalité : c'est un geste distinct, propre au Calendrier.
     */
    fun onSwipe(direction: SwipeDirection) {
        _cursor.value = when (direction) {
            SwipeDirection.BACKWARD -> _cursor.value.previous()
            SwipeDirection.FORWARD -> _cursor.value.next()
        }
    }

    /**
     * Ramène le curseur sur la journée en cours, sans changer de temporalité.
     *
     * Déclenché par un maintien long sur le dodécaèdre : c'est le raccourci qui permet
     * de rentrer après s'être éloigné dans le temps, sans avoir à revenir swipe par swipe.
     */
    fun resetToToday() {
        _cursor.value = TimeCursor.of(_cursor.value.scale, today())
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                HomeViewModel(
                    planning = application.container.planningRepository,
                    settings = application.container.settingsRepository,
                )
            }
        }
    }
}
