package com.astro.app.core.settings

import com.astro.app.core.config.PlanetColorDefaults
import java.time.DayOfWeek
import java.util.Locale

/** Comment la couleur d'une planète-jour est décidée. */
enum class PlanetColorMode {
    /** Deux couleurs : une pour les jours de semaine, une pour le week-end. */
    WEEKDAY_WEEKEND,

    /** Une couleur distincte pour chacun des sept jours. */
    PER_DAY,
}

/**
 * Règle de couleur déclenchée par un mot.
 *
 * Si le planning de la journée contient [keyword], la planète prend [color], quel que
 * soit le mode. C'est une exception délibérée : elle sert à signaler une journée
 * particulière sans changer le réglage général.
 */
data class KeywordColorRule(val keyword: String, val color: Int)

/** Réglages de couleur des planètes-jours. Couleurs en ARGB entier. */
data class PlanetColors(
    val mode: PlanetColorMode = PlanetColorMode.WEEKDAY_WEEKEND,
    val weekday: Int = PlanetColorDefaults.WEEKDAY,
    val weekend: Int = PlanetColorDefaults.WEEKEND,
    val perDay: Map<DayOfWeek, Int> = PlanetColorDefaults.PER_DAY,
    val keywordRules: List<KeywordColorRule> = emptyList(),
)

/**
 * Résolution de la couleur d'une planète-jour.
 *
 * Ordre de priorité, volontairement explicite :
 *  1. la première règle de mot-clé dont le mot apparaît dans le texte ;
 *  2. à défaut, la couleur du mode courant.
 *
 * La comparaison ignore la casse. Elle ne normalise pas les accents : « reunion » ne
 * déclenche pas une règle « réunion ». Ce choix est assumé plutôt que masqué — une
 * normalisation partielle surprendrait davantage qu'une absence de normalisation.
 *
 * Objet pur, testable sans Android.
 */
object PlanetColorResolver {

    fun colorFor(day: DayOfWeek, text: String, colors: PlanetColors): Int {
        val haystack = text.lowercase(Locale.ROOT)
        val matched = colors.keywordRules.firstOrNull { rule ->
            rule.keyword.isNotBlank() && haystack.contains(rule.keyword.lowercase(Locale.ROOT))
        }
        if (matched != null) return matched.color

        return when (colors.mode) {
            PlanetColorMode.WEEKDAY_WEEKEND ->
                if (isWeekend(day)) colors.weekend else colors.weekday
            PlanetColorMode.PER_DAY ->
                colors.perDay[day] ?: PlanetColorDefaults.PER_DAY.getValue(day)
        }
    }

    private fun isWeekend(day: DayOfWeek): Boolean =
        day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
}
