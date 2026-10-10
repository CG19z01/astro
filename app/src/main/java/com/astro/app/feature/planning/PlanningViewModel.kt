package com.astro.app.feature.planning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.planning.PlanningEntry
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.navigation.PLANNING_DATE_ARG
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Planning d'une date précise, en lignes.
 *
 * **Une seule source pendant l'édition.** Les lignes sont lues UNE fois dans le stockage,
 * puis vivent ici : une écriture qui revient du disque ne vient pas écraser une saisie
 * en cours, et la saisie survit aux recompositions comme à la rotation de l'écran.
 *
 * **Enregistrement.** Automatique, après [PlanningConfig.AUTOSAVE_DELAY_MILLIS] sans
 * modification, dans l'ordre garanti par [PlanningAutosave]. En quittant la page, ce
 * qui n'a pas encore été écrit l'est aussitôt, dans [flushScope] : la portée du
 * ViewModel étant annulée à sa destruction, une saisie faite juste avant Retour serait
 * sinon perdue.
 */
@OptIn(FlowPreview::class)
class PlanningViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PlanningRepository,
    private val flushScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    autosaveDelayMillis: Long = PlanningConfig.AUTOSAVE_DELAY_MILLIS,
) : ViewModel() {

    val date: LocalDate = LocalDate.parse(
        requireNotNull(savedStateHandle.get<String>(PLANNING_DATE_ARG)) {
            "Route planning appelée sans argument $PLANNING_DATE_ARG"
        },
        DateTimeFormatter.ISO_LOCAL_DATE,
    )

    /** Lignes d'édition, ligne vide finale comprise. `null` tant qu'elles ne sont pas lues. */
    private val _lines = MutableStateFlow<List<PlanningEntry>?>(null)
    val lines: StateFlow<List<PlanningEntry>?> = _lines.asStateFlow()

    private val autosave = PlanningAutosave { entries -> repository.saveEntries(date, entries) }
    private val requests = MutableStateFlow<Pair<Long, List<PlanningEntry>>?>(null)

    init {
        viewModelScope.launch {
            _lines.value = PlanningLines.fromStored(repository.entriesFor(date).first())
        }
        viewModelScope.launch {
            requests.filterNotNull().debounce(autosaveDelayMillis).collect { (revision, entries) ->
                autosave.persist(revision, entries)
            }
        }
    }

    fun onTextChange(id: Long, text: String) = edit { PlanningLines.withText(it, id, text) }

    fun onTimeChange(id: Long, time: LocalTime?) = edit { PlanningLines.withTime(it, id, time) }

    /** La ligne a perdu le focus : vide et au milieu, elle disparaît. */
    fun onLineLeft(id: Long) = edit { PlanningLines.dropIfEmpty(it, id) }

    private fun edit(transform: (List<PlanningEntry>) -> List<PlanningEntry>) {
        val before = _lines.value ?: return
        val after = transform(before)
        _lines.value = after
        val toPersist = PlanningLines.toPersist(after)
        // Rien à écrire si seule la ligne vide finale, ou une heure sans texte, a changé.
        if (toPersist != PlanningLines.toPersist(before)) {
            requests.value = autosave.request(toPersist) to toPersist
        }
    }

    override fun onCleared() {
        flushScope.launch { autosave.flush() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                PlanningViewModel(createSavedStateHandle(), application.container.planningRepository)
            }
        }
    }
}
