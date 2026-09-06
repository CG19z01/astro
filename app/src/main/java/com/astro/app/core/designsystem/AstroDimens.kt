package com.astro.app.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Échelles d'espacement, de rayons et de traits.
 *
 * Une seule progression pour toute l'application : c'est ce qui fait qu'un écran
 * ressemble au suivant. Aucune valeur en dur dans les écrans.
 */
@Immutable
data class AstroDimens(
    val spaceXs: Dp = 4.dp,
    val spaceSm: Dp = 8.dp,
    val spaceMd: Dp = 16.dp,
    val spaceLg: Dp = 24.dp,
    val spaceXl: Dp = 36.dp,
    val spaceXxl: Dp = 56.dp,

    /** Marge latérale commune à tous les écrans. */
    val screenPadding: Dp = 24.dp,

    val radiusSm: Dp = 12.dp,
    val radiusMd: Dp = 20.dp,
    val radiusLg: Dp = 28.dp,
    val radiusXl: Dp = 36.dp,

    /** Trait des figures dessinées : orbites, astres, solide 3D. */
    val hairline: Dp = 1.dp,
    val strokeThin: Dp = 1.2.dp,
    val strokeRegular: Dp = 1.6.dp,

    val iconSm: Dp = 18.dp,
    val iconMd: Dp = 22.dp,
)
