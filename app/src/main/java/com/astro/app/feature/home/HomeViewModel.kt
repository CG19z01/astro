package com.astro.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.astro.app.AstroApplication
import com.astro.app.core.alarm.NextAlarmSource
import com.astro.app.core.alarm.WakeAlarm
import com.astro.app.core.config.AlarmConfig
import com.astro.app.core.config.DayWheelConfig
import com.astro.app.core.phrases.DailyPhrase
import com.astro.app.core.phrases.Phrase
import com.astro.app.core.planning.DayPlanning
import com.astro.app.core.planning.PlanningRepository
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * État de la page d'accueil : où l'on se trouve dans le temps, et ce que contient le
 * planning de la période affichée.
 *
 * Le curseur vit dans le ViewModel et non dans la composition : il survit ainsi à une
 * rotation de l'écran et à un aller-retour vers une autre page, ce qui est le
 * comportement attendu d'une navigation temporelle.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val planning: PlanningRepository,
    settings: SettingsRepository,
    /**
     * Source de la date du jour. Injectée plutôt que lue directement : sans cela, le
     * retour à aujourd'hui dépendrait de l'horloge système et ne serait pas testable.
     */
    private val today: () -> LocalDate = LocalDate::now,
    /** Prochain réveil de l'appareil ; par défaut, aucun. */
    nextAlarm: NextAlarmSource = NextAlarmSource { flowOf(null) },
    /** Fuseau de l'appareil : celui dans lequel sonne le réveil. */
    private val zone: () -> ZoneId = ZoneId::systemDefault,
    /** Phrase du jour ; par défaut, aucune. */
    dailyPhrase: DailyPhrase? = null,
) : ViewModel() {

    /** Phrase du jour, tirée de la liste importée par l'utilisateur ; nulle sans liste. */
    val phraseOfDay: StateFlow<Phrase?> = (dailyPhrase?.ofToday() ?: flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    private val _cursor = MutableStateFlow(TimeCursor.of(TimeScale.INITIAL, today()))
    val cursor: StateFlow<TimeCursor> = _cursor.asStateFlow()

    /**
     * Entrées des journées de la roue, de la période courante à la dernière affichée,
     * suivies en continu : une écriture se voit aussitôt.
     *
     * Elles servent aux messages affichés dans chaque planète et à sa couleur, une règle
     * de mot pouvant teindre n'importe laquelle d'entre elles.
     */
    val dayPlannings: StateFlow<List<DayPlanning>> = _cursor
        .flatMapLatest { current ->
            combine((0 until DayWheelConfig.DAY_COUNT).map { offset ->
                planning.dayFor(current.shifted(offset.toLong()).anchor)
            }) { entries -> entries.toList() }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            List(DayWheelConfig.DAY_COUNT) { DayPlanning() },
        )

    /** Notes sans heure en haut des planètes plutôt qu'en bas (réglage des Paramètres). */
    val untimedNotesFirst: StateFlow<Boolean> = settings.preferences
        .map { it.untimedNotesFirst }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), false)

    /**
     * Premier réveil de la journée portée par la planète J+1 ([AlarmConfig.PLANET_OFFSET]),
     * [WakeAlarm.Unknown] si le réglage est coupé. Suit le curseur et les changements de
     * réveil annoncés par Android.
     */
    val wakeAlarm: StateFlow<WakeAlarm> = combine(
        _cursor,
        settings.preferences.map { it.showNextAlarm },
        nextAlarm.nextAlarm(),
    ) { cursor, shown, next ->
        if (!shown) return@combine WakeAlarm.Unknown
        val date = cursor.shifted(AlarmConfig.PLANET_OFFSET.toLong()).anchor
        WakeAlarm.on(date, today(), next, zone())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), WakeAlarm.Unknown)

    /** Réglages de couleur des planètes-jours. */
    val planetColors: StateFlow<PlanetColors> = settings.preferences
        .map { it.planetColors }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PlanetColors())

    /**
     * Déplace le curseur DANS la temporalité courante.
     * Ne change jamais de temporalité : c'est un geste distinct, propre au Calendrier.
     */
    fun onSwipe(direction: SwipeDirection) {
        _cursor.value = when (direction) {
            SwipeDirection.BACKWARD -> _cursor.value.previous()
            SwipeDirection.FORWARD -> _cursor.value.next()
        }
    }

    /**
     * Ramène le curseur sur la journée en cours, sans changer de temporalité.
     *
     * Déclenché par un maintien long sur le dodécaèdre : c'est le raccourci qui permet
     * de rentrer après s'être éloigné dans le temps, sans avoir à revenir swipe par swipe.
     */
    fun resetToToday() {
        _cursor.value = TimeCursor.of(_cursor.value.scale, today())
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AstroApplication
                HomeViewModel(
                    planning = application.container.planningRepository,
                    settings = application.container.settingsRepository,
                    nextAlarm = application.container.nextAlarmSource,
                    dailyPhrase = DailyPhrase(application.container.phraseRepository),
                )
            }
        }
    }
}
