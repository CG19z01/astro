package com.astro.app.core.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.astro.app.core.config.SpeechConfig
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lecture par le moteur de synthèse vocale du téléphone (`TextToSpeech`).
 *
 * Aucune dépendance, aucun service payant : le moteur est celui installé sur
 * l'appareil (Google sur l'émulateur). Démarré à la première demande seulement.
 */
class AndroidSpeechService(private val context: Context) : SpeechService {

    private val _state = MutableStateFlow(SpeechState.STARTING)
    override val state: StateFlow<SpeechState> = _state.asStateFlow()
    private val _voices = MutableStateFlow<List<SpeechVoice>>(emptyList())
    override val voices: StateFlow<List<SpeechVoice>> = _voices.asStateFlow()

    private var engine: TextToSpeech? = null
    private var lastUtterance: String? = null
    private var counter = 0

    override fun prepare() {
        if (engine != null) return
        engine = TextToSpeech(context.applicationContext) { status -> onReady(status) }
    }

    private fun onReady(status: Int) {
        val tts = engine
        val language = tts?.setLanguage(Locale.forLanguageTag(SpeechConfig.LANGUAGE_TAG))
        if (status != TextToSpeech.SUCCESS || tts == null || language == null || language < 0) {
            _state.value = SpeechState.UNAVAILABLE
            return
        }
        tts.setOnUtteranceProgressListener(Progress())
        _voices.value = tts.voices.orEmpty()
            .filter { it.locale.language == SpeechConfig.VOICE_LANGUAGE && it.locale.country == SpeechConfig.VOICE_COUNTRY }
            .sortedBy { it.name }
            .map { voice ->
                SpeechVoice(name = voice.name, needsNetwork = voice.isNetworkConnectionRequired)
            }
        _state.value = SpeechState.READY
    }

    override fun speak(text: String, voiceName: String?) {
        val tts = engine ?: return prepare()
        if (_state.value == SpeechState.STARTING || _state.value == SpeechState.UNAVAILABLE) return
        tts.stop()
        tts.setLanguage(Locale.forLanguageTag(SpeechConfig.LANGUAGE_TAG))
        tts.voices.orEmpty().firstOrNull { it.name == voiceName }?.let { tts.voice = it }
        val chunks = SpeechChunks.split(text, TextToSpeech.getMaxSpeechInputLength())
        if (chunks.isEmpty()) return
        val batch = ++counter
        chunks.forEachIndexed { index, chunk ->
            val id = "astro-$batch-$index"
            if (index == chunks.lastIndex) lastUtterance = id
            tts.speak(chunk, TextToSpeech.QUEUE_ADD, null, id)
        }
        _state.value = SpeechState.SPEAKING
    }

    override fun stop() {
        lastUtterance = null
        engine?.stop()
        if (_state.value == SpeechState.SPEAKING) _state.value = SpeechState.READY
    }

    /** Appelée depuis un fil du moteur : on ne touche qu'aux StateFlow, sûrs entre fils. */
    private inner class Progress : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) = Unit

        override fun onDone(utteranceId: String?) {
            if (utteranceId == lastUtterance) _state.value = SpeechState.READY
        }

        @Deprecated("Remplacée par onError(String, Int), toujours abstraite dans l'API.")
        override fun onError(utteranceId: String?) {
            _state.value = SpeechState.READY
        }
    }
}
