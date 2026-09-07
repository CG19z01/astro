package com.astro.app.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Palette de l'application, décrite par rôles et non par couleurs.
 *
 * Les composants n'écrivent jamais une couleur : ils demandent un rôle. Les deux
 * ambiances exposent exactement les mêmes rôles, donc aucun composant n'a besoin
 * de savoir s'il est affiché en sombre ou en clair.
 *
 * Ajouter une palette = ajouter une instance ici. Aucun écran à modifier.
 */
@Immutable
data class AstroPalette(
    val name: String,
    val isDark: Boolean,
    /**
     * Fond spatial uni. Employé tel quel, sans dégradé : c'est le noir profond de
     * l'ambiance nocturne. Rôle distinct de [skyTop] et consorts, qui servent aussi
     * aux billes, au solide 3D et à la barre de navigation — les confondre changerait
     * ces éléments en même temps que le fond.
     */
    val space: Color,
    /** Dégradé de fond, du haut vers le bas. */
    val skyTop: Color,
    val skyMid: Color,
    val skyBottom: Color,
    /** Halos diffus posés sur le fond. Toujours très peu opaques. */
    val haloSolar: Color,
    val haloLunar: Color,
    /** Surfaces posées sur le fond : cartes, panneaux. */
    val surface: Color,
    val surfaceBorder: Color,
    /** Trois niveaux de texte, du plus lisible au plus discret. */
    val ink: Color,
    val inkMuted: Color,
    val inkFaint: Color,
    /** Accents. À employer avec parcimonie : ils perdent leur force s'ils sont partout. */
    val accentSolar: Color,
    val accentLunar: Color,
    val accentCosmic: Color,
    /** Traits structurels : orbites, constellations, séparateurs. */
    val orbit: Color,
    val star: Color,
)

/**
 * Nuit profonde. Ce n'est pas un fond noir : c'est un bleu de nuit très désaturé qui
 * se réchauffe vers le bas, comme un horizon. Le noir pur écrase la profondeur.
 */
val AstroNightPalette = AstroPalette(
    name = "Nuit profonde",
    isDark = true,
    space = Color(0xFF030408),
    skyTop = Color(0xFF05070F),
    skyMid = Color(0xFF0B1024),
    skyBottom = Color(0xFF151A3A),
    haloSolar = Color(0xFFE9C68C),
    haloLunar = Color(0xFF7E8FE8),
    surface = Color(0xFF141A33),
    surfaceBorder = Color(0xFF2A3358),
    ink = Color(0xFFEDEFFA),
    inkMuted = Color(0xFF9BA3C8),
    inkFaint = Color(0xFF646C93),
    accentSolar = Color(0xFFE8C48A),
    accentLunar = Color(0xFF9DAEFF),
    accentCosmic = Color(0xFFB794F6),
    orbit = Color(0xFF2E3866),
    star = Color(0xFFDCE2FF),
)

/**
 * Aube céleste. Ni blanc, ni gris : un crème chaud qui bascule vers un lavande pâle,
 * l'inverse exact de la nuit — la lumière vient du haut au lieu de venir du bas.
 */
val AstroDawnPalette = AstroPalette(
    name = "Aube céleste",
    isDark = false,
    // L'ambiance claire garde son dégradé : ce rôle n'y sert pas de fond.
    space = Color(0xFFFDF7F0),
    skyTop = Color(0xFFFDF7F0),
    skyMid = Color(0xFFF4EEF6),
    skyBottom = Color(0xFFE6E2F5),
    haloSolar = Color(0xFFF0C89B),
    haloLunar = Color(0xFFB9BEEA),
    surface = Color(0xFFFFFBF6),
    surfaceBorder = Color(0xFFDCD4E8),
    ink = Color(0xFF221D3B),
    inkMuted = Color(0xFF635D82),
    inkFaint = Color(0xFF9A93B4),
    accentSolar = Color(0xFFC07A3E),
    accentLunar = Color(0xFF5A5FB0),
    accentCosmic = Color(0xFF8A63C4),
    orbit = Color(0xFFCFC6E2),
    star = Color(0xFFB4A9D0),
)
