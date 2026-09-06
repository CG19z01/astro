package com.astro.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.config.FeatureId
import com.astro.app.core.settings.FeatureStates
import com.astro.app.core.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Expose et modifie les réglages. Ne connaît que l'interface [SettingsRepository]. */
class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {

    val featureStates: StateFlow<Map<FeatureId, Boolean>> = repository.featureStates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = FeatureStates.defaults,
        )

    fun setFeatureEnabled(id: FeatureId, enabled: Boolean) {
        viewModelScope.launch { repository.setFeatureEnabled(id, enabled) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                SettingsViewModel(application.container.settingsRepository)
            }
        }
    }
}
