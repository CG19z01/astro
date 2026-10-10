package com.astro.app.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * État de la page Calendrier.
 *
 * **Vue initiale.** Elle vient de la préférence enregistrée, lue UNE fois à l'ouverture.
 * Tant qu'elle n'est pas lue, [state] vaut `null` et la grille n'est pas dessinée : on
 * évite ainsi d'afficher une vue par défaut pour basculer aussitôt sur la bonne.
 * Changer de vue ensuite n'écrit rien : la préférence ne change que dans les Paramètres.
 *
 * **Durée de vie.** Ce ViewModel appartient à l'entrée de navigation du Calendrier. La
 * quitter pour une autre page la détruit, et la prochaine ouverture repart de la
 * préférence, centrée sur aujourd'hui. Ouvrir un planning depuis le Calendrier, en
 * revanche, empile la page par-dessus : au retour, on retrouve la vue et le jour laissés.
 */
class CalendarViewModel(
    planning: PlanningRepository,
    settings: SettingsRepository,
    /** Injectée pour que l'ouverture « sur aujourd'hui » soit testable. */
    private val today: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    private val _state = MutableStateFlow<CalendarState?>(null)
    val state: StateFlow<CalendarState?> = _state.asStateFlow()

    private val swipes = CalendarSwipeHandler()

    val preferences: StateFlow<UserPreferences> = settings.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    /** Plannings enregistrés, suivis en continu : une note sauvegardée se voit aussitôt. */
    val entries: StateFlow<Map<LocalDate, String>> = planning.allEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyMap())

    init {
        viewModelScope.launch {
            val initial = settings.preferences.first().calendarInitialScale
            _state.value = CalendarState.of(initial, today())
        }
    }

    fun todayDate(): LocalDate = today()

    fun onSwipe(direction: SwipeDirection, nowMillis: Long) =
        update { swipes.onSwipe(it, direction, nowMillis) }

    fun onSelectScale(scale: TimeScale) {
        swipes.reset()
        update { it.withScale(scale) }
    }

    /**
     * Appui sur un jour. Le premier appui le sélectionne ; un second appui sur le jour
     * déjà sélectionné ouvre son planning, signalé par le retour `true`.
     */
    fun onDayTapped(date: LocalDate): Boolean {
        val current = _state.value ?: return false
        if (current.selected == date) return true
        _state.value = current.select(date)
        return false
    }

    fun onMonthTapped(month: YearMonth) {
        swipes.reset()
        update { it.openMonth(month) }
    }

    private fun update(transform: (CalendarState) -> CalendarState) {
        _state.value = _state.value?.let(transform)
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                CalendarViewModel(
                    planning = application.container.planningRepository,
                    settings = application.container.settingsRepository,
                )
            }
        }
    }
}
