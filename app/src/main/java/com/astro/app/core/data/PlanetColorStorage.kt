package com.astro.app.core.data

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.astro.app.core.config.PlanetColorDefaults
import com.astro.app.core.settings.KeywordColorRule
import com.astro.app.core.settings.PlanetColorMode
import com.astro.app.core.settings.PlanetColors
import java.time.DayOfWeek

private val MODE = stringPreferencesKey("planet_color_mode")
private val WEEKDAY = intPreferencesKey("planet_color_weekday")
private val WEEKEND = intPreferencesKey("planet_color_weekend")
private val RULES = stringPreferencesKey("planet_color_rules")

private fun dayKey(day: DayOfWeek) = intPreferencesKey("planet_color_day_${day.name}")

/** Longueur de la couleur en tête d'une règle sérialisée : huit chiffres hexadécimaux ARGB. */
private const val COLOR_PREFIX_LENGTH = 8

/**
 * Lecture et écriture des couleurs de planètes dans DataStore.
 *
 * Les règles de mot-clé sont sérialisées en lignes, la couleur en préfixe **de longueur
 * fixe** suivie du mot. Ce format évite tout séparateur entre les deux champs : un mot
 * contenant le séparateur ne peut donc pas casser la lecture. Seuls les retours à la
 * ligne sont interdits dans un mot, et ils sont retirés à l'écriture.
 *
 * Extrait dans son propre fichier : le dépôt de préférences a déjà sa part, et mêler
 * les deux le rendrait illisible.
 */
internal fun readPlanetColors(stored: Preferences): PlanetColors = PlanetColors(
    mode = stored[MODE]?.let { name -> runCatching { PlanetColorMode.valueOf(name) }.getOrNull() }
        ?: PlanetColorMode.WEEKDAY_WEEKEND,
    weekday = stored[WEEKDAY] ?: PlanetColorDefaults.WEEKDAY,
    weekend = stored[WEEKEND] ?: PlanetColorDefaults.WEEKEND,
    perDay = DayOfWeek.entries.associateWith { day ->
        stored[dayKey(day)] ?: PlanetColorDefaults.PER_DAY.getValue(day)
    },
    keywordRules = stored[RULES]?.let(::decodeRules).orEmpty(),
)

internal fun MutablePreferences.writePlanetColors(colors: PlanetColors) {
    this[MODE] = colors.mode.name
    this[WEEKDAY] = colors.weekday
    this[WEEKEND] = colors.weekend
    colors.perDay.forEach { (day, color) -> this[dayKey(day)] = color }
    this[RULES] = encodeRules(colors.keywordRules)
}

internal fun encodeRules(rules: List<KeywordColorRule>): String = rules
    .filter { it.keyword.isNotBlank() }
    .joinToString("\n") { rule ->
        val color = (rule.color.toLong() and 0xFFFFFFFFL).toString(16).padStart(COLOR_PREFIX_LENGTH, '0')
        color + rule.keyword.replace("\n", " ").trim()
    }

internal fun decodeRules(raw: String): List<KeywordColorRule> = raw
    .split("\n")
    .filter { it.length > COLOR_PREFIX_LENGTH }
    .mapNotNull { line ->
        val color = line.take(COLOR_PREFIX_LENGTH).toLongOrNull(16)?.toInt() ?: return@mapNotNull null
        KeywordColorRule(keyword = line.drop(COLOR_PREFIX_LENGTH), color = color)
    }
