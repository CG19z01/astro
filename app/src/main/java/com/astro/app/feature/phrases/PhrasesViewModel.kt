package com.astro.app.feature.phrases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.phrases.DailyPhrase
import com.astro.app.core.phrases.Phrase
import com.astro.app.core.phrases.PhraseFileParser
import com.astro.app.core.phrases.PhraseAudience
import com.astro.app.core.phrases.PhraseImport
import com.astro.app.core.phrases.PhraseWishes
import com.astro.app.core.phrases.ReaderGender
import com.astro.app.core.phrases.PhraseRepository
import com.astro.app.core.settings.SettingsRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Issue du dernier import, pour le message affiché sous le bouton. */
sealed interface ImportStatus {
    data class Imported(val count: Int) : ImportStatus
    data class Refused(val failure: PhraseImport.Failure) : ImportStatus
    data object TooLarge : ImportStatus
    data object Unreadable : ImportStatus
}

/**
 * Page des phrases : la liste importée et l'historique des phrases du jour.
 *
 * L'historique est le journal, du plus récent au plus ancien. La phrase du jour y est
 * inscrite même si l'on arrive ici sans passer par l'accueil.
 */
class PhrasesViewModel(
    private val repository: PhraseRepository,
    private val settings: SettingsRepository,
    dailyPhrase: DailyPhrase,
) : ViewModel() {

    /** Genre déclaré et thèmes voulus. */
    val wishes: StateFlow<PhraseWishes> = settings.preferences.map { it.phraseWishes }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PhraseWishes())

    /** Thèmes présents dans la liste importée. */
    val themes: StateFlow<List<String>> = repository.phrases.map(PhraseAudience::themes)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    /** Favoris, le plus récent en premier. */
    val favorites: StateFlow<List<Phrase>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    fun onToggleFavorite(phrase: Phrase) {
        viewModelScope.launch { repository.toggleFavorite(phrase) }
    }

    /**
     * Masculin ou Féminin s'ajoutent à « Autre », toujours inclus (choix de l'utilisateur
     * du 2026-10-15). Toucher à nouveau le genre choisi le retire : il ne reste qu'« Autre ».
     */
    fun onReaderGender(gender: ReaderGender) {
        viewModelScope.launch {
            settings.updatePreferences {
                it.copy(readerGender = if (it.readerGender == gender) ReaderGender.OTHER else gender)
            }
        }
    }

    /** Ajoute ou retire un thème ; [theme] nul : tous les thèmes. */
    fun onToggleTheme(theme: String?) {
        viewModelScope.launch {
            settings.updatePreferences { prefs ->
                val themes = when {
                    theme == null -> emptySet()
                    prefs.phraseThemes.any { it.equals(theme, ignoreCase = true) } ->
                        prefs.phraseThemes.filterNot { it.equals(theme, ignoreCase = true) }.toSet()
                    else -> prefs.phraseThemes + theme
                }
                prefs.copy(phraseThemes = themes)
            }
        }
    }

    /** Nombre de phrases importées. */
    val count: StateFlow<Int> = repository.phrases.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), 0)

    /** Journal, du plus récent au plus ancien. */
    val history: StateFlow<List<Pair<LocalDate, Phrase>>> =
        combine(repository.journal, dailyPhrase.ofToday()) { journal, _ ->
            journal.entries.sortedByDescending { it.key }.map { it.key to it.value }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    private val _status = MutableStateFlow<ImportStatus?>(null)
    val status: StateFlow<ImportStatus?> = _status.asStateFlow()

    /** Le fichier choisi a été lu (ou non) : on l'analyse, et on remplace la liste. */
    fun onFileRead(read: FileRead) {
        viewModelScope.launch {
            _status.value = when (read) {
                FileRead.TooLarge -> ImportStatus.TooLarge
                FileRead.Unreadable -> ImportStatus.Unreadable
                is FileRead.Text -> when (val result = PhraseFileParser.parse(read.content)) {
                    is PhraseImport.Failure -> ImportStatus.Refused(result)
                    is PhraseImport.Success -> {
                        repository.replacePhrases(result.phrases)
                        ImportStatus.Imported(result.phrases.size)
                    }
                }
            }
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                val container = application.container
                PhrasesViewModel(container.phraseRepository, container.settingsRepository, container.dailyPhrase())
            }
        }
    }
}
