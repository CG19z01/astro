package com.astro.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.astro.app.R

/**
 * Typographie de l'application.
 *
 * Deux familles, choisies pour se compléter plutôt que se ressembler :
 *  - **Cormorant Garamond** pour les titres. Serif à fort contraste, élégant, il
 *    évoque les planches d'astronomie anciennes sans tomber dans le décoratif.
 *    Réservé aux grandes tailles : ses déliés fins deviennent fragiles en petit corps.
 *  - **Space Grotesk** pour l'interface et le texte courant. Grotesque moderne, avec
 *    du caractère dans ses détails, mais dessiné pour être lu à l'écran.
 *
 * Les deux sont sous licence SIL Open Font License 1.1 (copies dans `assets/licenses`),
 * embarquées dans l'APK : aucun téléchargement à l'exécution, donc aucun écran qui
 * change de police après coup.
 *
 * Ce sont des polices variables : les graisses sont obtenues par l'axe `wght`, ce qui
 * évite d'embarquer un fichier par graisse.
 */
private fun cormorant(weight: Int) = Font(
    resId = R.font.cormorant_garamond,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private fun grotesk(weight: Int) = Font(
    resId = R.font.space_grotesk,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private fun nunitoItalic(weight: Int) = Font(
    resId = R.font.nunito_italic,
    weight = FontWeight(weight),
    style = FontStyle.Italic,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

/** Nunito italique, réservée aux phrases (demande du 2026-10-15). Licence OFL 1.1. */
val PhraseFamily = FontFamily(nunitoItalic(400), nunitoItalic(600))

/**
 * Une phrase de l'historique : plus petite que les titres, en Nunito italique — une
 * pensée qu'on lit, pas un titre de section.
 */
val PhraseStyle = TextStyle(
    fontFamily = PhraseFamily, fontWeight = FontWeight(400), fontStyle = FontStyle.Italic,
    fontSize = 16.sp, lineHeight = 23.sp,
)

val DisplayFamily = FontFamily(cormorant(300), cormorant(400), cormorant(500), cormorant(600))
val BodyFamily = FontFamily(grotesk(300), grotesk(400), grotesk(500), grotesk(700))

/**
 * Hiérarchie typographique.
 *
 * `display*` et `headline*` : titres en Cormorant, interlettrage légèrement ouvert.
 * `title*`, `body*`, `label*` : Space Grotesk.
 * `labelSmall` sert d'exergue : petites capitales très espacées, pour les libellés
 * de section — c'est ce qui donne le ton « carte astronomique ».
 */
val AstroTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = DisplayFamily, fontWeight = FontWeight(300),
        fontSize = 46.sp, lineHeight = 52.sp, letterSpacing = 0.5.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = DisplayFamily, fontWeight = FontWeight(300),
        fontSize = 36.sp, lineHeight = 42.sp, letterSpacing = 0.4.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = DisplayFamily, fontWeight = FontWeight(400),
        fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = 0.3.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = DisplayFamily, fontWeight = FontWeight(400),
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = 0.3.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(500),
        fontSize = 19.sp, lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(500),
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(400),
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(400),
        fontSize = 14.sp, lineHeight = 21.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(400),
        fontSize = 12.5.sp, lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(500),
        fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.4.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(500),
        fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.6.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFamily, fontWeight = FontWeight(500),
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 2.2.sp,
    ),
)
