package com.astro.app.feature.manifestation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.manifestation.Manifestation
import com.astro.app.core.manifestation.ManifestationRepository
import com.astro.app.core.manifestation.SubjectFilter
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.speech.SpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Liste, lecture et édition des manifestations, et leur lecture à voix haute. */
class ManifestationViewModel(
    private val repository: ManifestationRepository,
    private val settings: SettingsRepository,
    private val speech: SpeechService,
) : ViewModel() {

    private val _page = MutableStateFlow<ManifestationPage>(ManifestationPage.List)
    val page: StateFlow<ManifestationPage> = _page.asStateFlow()

    private val _subject = MutableStateFlow<String?>(null)
    /** Sujet du filtre ; nul, toutes les manifestations. */
    val subject: StateFlow<String?> = _subject.asStateFlow()

    val all: StateFlow<List<Manifestation>> = repository.all.stateIn(viewModelScope, STARTED, emptyList())

    val subjects: StateFlow<List<String>> = all.map(SubjectFilter::subjects).stateIn(viewModelScope, STARTED, emptyList())

    val shown: StateFlow<List<Manifestation>> = combine(all, _subject) { list, subject ->
        SubjectFilter.filter(list, subject)
    }.stateIn(viewModelScope, STARTED, emptyList())

    val speechState = speech.state
    val voices = speech.voices
    val voiceName: StateFlow<String?> = settings.preferences.map { it.speechVoiceName }
        .stateIn(viewModelScope, STARTED, null)

    init {
        speech.prepare()
    }

    fun onFilter(subject: String?) {
        _subject.value = subject
    }

    fun onAdd() {
        _page.value = ManifestationPage.Edit(Manifestation(title = "", subject = _subject.value.orEmpty(), text = ""))
    }

    fun onOpen(id: Long) {
        _page.value = ManifestationPage.Read(id)
    }

    fun onEdit(manifestation: Manifestation) {
        speech.stop()
        _page.value = ManifestationPage.Edit(manifestation)
    }

    fun onDraftChange(draft: Manifestation) {
        if (_page.value is ManifestationPage.Edit) _page.value = ManifestationPage.Edit(draft)
    }

    /** Enregistre puis montre la manifestation. Rien à enregistrer : retour sans écrire. */
    fun onSave() {
        val draft = (_page.value as? ManifestationPage.Edit)?.draft ?: return
        if (draft.isBlank) return onBack()
        viewModelScope.launch {
            val id = repository.save(draft.copy(title = draft.title.trim(), subject = draft.subject.trim()))
            _page.value = ManifestationPage.Read(id)
        }
    }

    fun onDelete(id: Long) {
        speech.stop()
        viewModelScope.launch {
            repository.delete(id)
            _page.value = ManifestationPage.List
        }
    }

    /** Retour : de l'édition à la lecture (ou à la liste), de la lecture à la liste. */
    fun onBack() {
        speech.stop()
        _page.value = when (val current = _page.value) {
            is ManifestationPage.Edit ->
                if (current.draft.id != 0L) ManifestationPage.Read(current.draft.id) else ManifestationPage.List
            else -> ManifestationPage.List
        }
    }

    fun onRead(manifestation: Manifestation) {
        val spoken = listOf(manifestation.title, manifestation.text).filter { it.isNotBlank() }
        speech.speak(spoken.joinToString(".\n\n"), voiceName.value)
    }

    fun onStop() = speech.stop()

    fun onSelectVoice(name: String?) {
        viewModelScope.launch { settings.updatePreferences { it.copy(speechVoiceName = name) } }
    }

    fun onPreviewVoice(name: String?, sample: String) = speech.speak(sample, name)

    override fun onCleared() {
        speech.stop()
    }

    companion object {
        private val STARTED = SharingStarted.WhileSubscribed(5_000L)

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                ManifestationViewModel(
                    app.container.manifestationRepository,
                    app.container.settingsRepository,
                    app.container.speechService,
                )
            }
        }
    }
}
