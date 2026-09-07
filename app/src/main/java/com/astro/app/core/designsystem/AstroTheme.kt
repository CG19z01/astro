package com.astro.app.core.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.astro.app.core.config.Ambiance

val LocalAstroPalette = staticCompositionLocalOf { AstroNightPalette }
val LocalAstroDimens = staticCompositionLocalOf { AstroDimens() }

/** Accès court à la palette et aux mesures depuis n'importe quel composant. */
object AstroTheme {
    val palette: AstroPalette
        @Composable get() = LocalAstroPalette.current

    val dimens: AstroDimens
        @Composable get() = LocalAstroDimens.current
}

/**
 * Thème de l'application.
 *
 * Chaque couleur est animée séparément lors d'un changement d'ambiance : passer de la
 * nuit à l'aube se fait par un fondu du fond, du texte et des accents, et non par une
 * bascule sèche. C'est ce qui rend le passage entre deux sections élégant.
 *
 * `MaterialTheme` est alimenté à partir de la même palette, afin que les composants
 * Material déjà utilisés — barre de navigation, interrupteurs — suivent l'ambiance
 * sans qu'on ait à les habiller un par un.
 */
@Composable
fun AstroTheme(ambiance: Ambiance, content: @Composable () -> Unit) {
    val target = when (ambiance) {
        Ambiance.DARK -> AstroNightPalette
        Ambiance.LIGHT -> AstroDawnPalette
    }
    val palette = target.animated()

    CompositionLocalProvider(
        LocalAstroPalette provides palette,
        LocalAstroDimens provides AstroDimens(),
    ) {
        MaterialTheme(
            colorScheme = palette.toColorScheme(),
            typography = AstroTypography,
            content = content,
        )
    }
}

@Composable
private fun AstroPalette.animated(): AstroPalette = copy(
    space = fade(space, "space"),
    skyTop = fade(skyTop, "skyTop"),
    skyMid = fade(skyMid, "skyMid"),
    skyBottom = fade(skyBottom, "skyBottom"),
    haloSolar = fade(haloSolar, "haloSolar"),
    haloLunar = fade(haloLunar, "haloLunar"),
    surface = fade(surface, "surface"),
    surfaceBorder = fade(surfaceBorder, "surfaceBorder"),
    ink = fade(ink, "ink"),
    inkMuted = fade(inkMuted, "inkMuted"),
    inkFaint = fade(inkFaint, "inkFaint"),
    accentSolar = fade(accentSolar, "accentSolar"),
    accentLunar = fade(accentLunar, "accentLunar"),
    accentCosmic = fade(accentCosmic, "accentCosmic"),
    orbit = fade(orbit, "orbit"),
    star = fade(star, "star"),
)

@Composable
private fun fade(target: Color, label: String): Color {
    val state: State<Color> = animateColorAsState(
        targetValue = target,
        animationSpec = AstroMotion.transition(),
        label = label,
    )
    return state.value
}

private fun AstroPalette.toColorScheme() = if (isDark) {
    darkColorScheme(
        primary = accentSolar, onPrimary = skyTop,
        secondary = accentLunar, onSecondary = skyTop,
        tertiary = accentCosmic,
        background = skyMid, onBackground = ink,
        surface = surface, onSurface = ink,
        surfaceVariant = surface, onSurfaceVariant = inkMuted,
        outline = surfaceBorder, outlineVariant = orbit,
    )
} else {
    lightColorScheme(
        primary = accentSolar, onPrimary = skyTop,
        secondary = accentLunar, onSecondary = skyTop,
        tertiary = accentCosmic,
        background = skyMid, onBackground = ink,
        surface = surface, onSurface = ink,
        surfaceVariant = surface, onSurfaceVariant = inkMuted,
        outline = surfaceBorder, outlineVariant = orbit,
    )
}
