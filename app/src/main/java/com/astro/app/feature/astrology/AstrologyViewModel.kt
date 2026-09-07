package com.astro.app.feature.astrology

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.location.ObservationPoint
import com.astro.app.core.location.ObservationPoints
import com.astro.app.core.location.SessionLocation
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * État de la page Astrologie.
 *
 * Elle n'a besoin que des préférences : le lieu, pour les horaires de la Lune, et le
 * fuseau, pour les afficher. La phase, elle, ne dépend d'aucun lieu.
 */
class AstrologyViewModel(
    repository: SettingsRepository,
    sessionLocation: SessionLocation,
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = repository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UserPreferences(),
        )

    /**
     * Lieu d'observation, position de session comprise : la Lune et le Soleil doivent
     * voir le même lieu dans une même session.
     */
    val observationPoint: StateFlow<ObservationPoint?> =
        combine(sessionLocation.point, repository.preferences) { session, prefs ->
            ObservationPoints.resolve(session, prefs)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = null,
        )

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                AstrologyViewModel(
                    repository = application.container.settingsRepository,
                    sessionLocation = application.container.sessionLocation,
                )
            }
        }
    }
}
