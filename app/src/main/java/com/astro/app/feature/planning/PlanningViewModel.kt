package com.astro.app.feature.planning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.navigation.PLANNING_DATE_ARG
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Planning d'une date précise.
 *
 * La date vient de l'argument de navigation, jamais d'une valeur écrite dans le code :
 * c'est la bille sur laquelle l'utilisateur a appuyé qui la détermine.
 */
class PlanningViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PlanningRepository,
) : ViewModel() {

    val date: LocalDate = LocalDate.parse(
        requireNotNull(savedStateHandle.get<String>(PLANNING_DATE_ARG)) {
            "Route planning appelée sans argument $PLANNING_DATE_ARG"
        },
        DateTimeFormatter.ISO_LOCAL_DATE,
    )

    val text: StateFlow<String> = repository.entryFor(date)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), "")

    fun save(value: String) {
        viewModelScope.launch { repository.save(date, value) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                PlanningViewModel(createSavedStateHandle(), application.container.planningRepository)
            }
        }
    }
}
