package com.astro.app.feature.manifestation

import com.astro.app.core.config.FeatureId
import com.astro.app.core.manifestation.Manifestation
import com.astro.app.core.manifestation.ManifestationRepository
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.speech.SpeechService
import com.astro.app.core.speech.SpeechState
import com.astro.app.core.speech.SpeechVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class MemoryManifestations : ManifestationRepository {
    override val all = MutableStateFlow<List<Manifestation>>(emptyList())
    override suspend fun save(manifestation: Manifestation): Long {
        val id = if (manifestation.id != 0L) manifestation.id else (all.value.maxOfOrNull { it.id } ?: 0) + 1
        all.value = all.value.filterNot { it.id == id } + manifestation.copy(id = id)
        return id
    }
    override suspend fun delete(id: Long) { all.value = all.value.filterNot { it.id == id } }
}

private class RecordingSpeech : SpeechService {
    val spoken = mutableListOf<Pair<String, String?>>()
    var stops = 0
    override val state = MutableStateFlow(SpeechState.READY)
    override val voices = MutableStateFlow<List<SpeechVoice>>(emptyList())
    override fun prepare() = Unit
    override fun speak(text: String, voiceName: String?) { spoken += text to voiceName }
    override fun stop() { stops++ }
}

private class Prefs(voice: String?) : SettingsRepository {
    override val featureStates = flowOf(emptyMap<FeatureId, Boolean>())
    override suspend fun setFeatureEnabled(id: FeatureId, enabled: Boolean) = Unit
    override val preferences = MutableStateFlow(UserPreferences(speechVoiceName = voice))
    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        preferences.value = transform(preferences.value)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ManifestationViewModelTest {

    private val repo = MemoryManifestations()
    private val speech = RecordingSpeech()
    private val prefs = Prefs("fr-fr-x-test")

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun model() = ManifestationViewModel(repo, prefs, speech).also { vm ->
        // Abonnements, comme le ferait l'écran.
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch { vm.all.collect {} }
        scope.launch { vm.voiceName.collect {} }
    }

    @Test
    fun `ajouter, enregistrer, puis lire`() {
        val vm = model()
        vm.onFilter("Calme")
        vm.onAdd()
        assertEquals("Calme", (vm.page.value as ManifestationPage.Edit).draft.subject)
        vm.onDraftChange(Manifestation(title = " Paix ", subject = "Calme", text = "Je respire."))
        vm.onSave()
        val id = (vm.page.value as ManifestationPage.Read).id
        assertEquals("Paix", repo.all.value.single { it.id == id }.title)

        vm.onRead(repo.all.value.single())
        assertEquals("Paix.\n\nJe respire." to "fr-fr-x-test", speech.spoken.single())
    }

    @Test
    fun `une saisie vide n'est pas enregistree`() {
        val vm = model()
        vm.onAdd()
        vm.onSave()
        assertEquals(ManifestationPage.List, vm.page.value)
        assertEquals(emptyList<Manifestation>(), repo.all.value)
    }

    @Test
    fun `retour depuis une modification revient a la lecture, et arrete la voix`() {
        val vm = model()
        vm.onEdit(Manifestation(7, "Titre", "", "Texte"))
        vm.onBack()
        assertEquals(ManifestationPage.Read(7), vm.page.value)
        vm.onBack()
        assertEquals(ManifestationPage.List, vm.page.value)
        assertEquals(true, speech.stops >= 2)
    }

    @Test
    fun `supprimer revient a la liste`() {
        val vm = model()
        vm.onAdd()
        vm.onDraftChange(Manifestation(title = "A", subject = "", text = ""))
        vm.onSave()
        vm.onDelete((vm.page.value as ManifestationPage.Read).id)
        assertEquals(ManifestationPage.List, vm.page.value)
        assertEquals(emptyList<Manifestation>(), repo.all.value)
    }

    @Test
    fun `le choix de voix est enregistre`() {
        val vm = model()
        vm.onSelectVoice("fr-fr-x-autre")
        assertEquals("fr-fr-x-autre", prefs.preferences.value.speechVoiceName)
    }
}
