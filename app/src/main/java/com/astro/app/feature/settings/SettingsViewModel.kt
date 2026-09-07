package com.astro.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.config.FeatureId
import com.astro.app.core.settings.FeatureStates
import com.astro.app.core.location.DeviceLocationProvider
import com.astro.app.core.location.GeoPoint
import com.astro.app.core.location.SessionLocation
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Expose et modifie les réglages. Ne connaît que l'interface [SettingsRepository]. */
/** Où en est la demande de position. */
enum class LocationRequest { IDLE, LOADING, UNAVAILABLE, DENIED }

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val locationProvider: DeviceLocationProvider,
    private val sessionLocation: SessionLocation,
) : ViewModel() {

    /** Position de session, en mémoire uniquement. */
    val sessionPoint: StateFlow<GeoPoint?> = sessionLocation.point

    private val _locationRequest = MutableStateFlow(LocationRequest.IDLE)
    val locationRequest: StateFlow<LocationRequest> = _locationRequest.asStateFlow()

    /**
     * Demande une position à l'appareil, une fois. Le résultat n'est jamais enregistré :
     * il vit dans [SessionLocation] et disparaît à la fermeture de l'application.
     */
    fun requestDeviceLocation() {
        _locationRequest.value = LocationRequest.LOADING
        viewModelScope.launch {
            val point = locationProvider.currentPoint()
            if (point == null) {
                _locationRequest.value = LocationRequest.UNAVAILABLE
            } else {
                sessionLocation.set(point)
                _locationRequest.value = LocationRequest.IDLE
            }
        }
    }

    fun onLocationDenied() {
        _locationRequest.value = LocationRequest.DENIED
    }

    fun forgetDeviceLocation() {
        sessionLocation.clear()
        _locationRequest.value = LocationRequest.IDLE
    }

    val featureStates: StateFlow<Map<FeatureId, Boolean>> = repository.featureStates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = FeatureStates.defaults,
        )

    val preferences: StateFlow<UserPreferences> = repository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UserPreferences(),
        )

    fun setFeatureEnabled(id: FeatureId, enabled: Boolean) {
        viewModelScope.launch { repository.setFeatureEnabled(id, enabled) }
    }

    fun update(transform: (UserPreferences) -> UserPreferences) {
        viewModelScope.launch { repository.updatePreferences(transform) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                SettingsViewModel(
                    repository = application.container.settingsRepository,
                    locationProvider = application.container.locationProvider,
                    sessionLocation = application.container.sessionLocation,
                )
            }
        }
    }
}
