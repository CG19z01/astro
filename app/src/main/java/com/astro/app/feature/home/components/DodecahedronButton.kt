package com.astro.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import kotlinx.coroutines.delay

/**
 * Dodécaèdre 3D en rotation.
 *
 * Deux gestes : un appui ouvre la page Manifestation, un maintien long ramène la
 * navigation temporelle sur la journée en cours.
 *
 * Rester appuyé emballe le solide : la rotation accélère fortement et un troisième axe
 * entre en jeu. L'emballement se prolonge quelques secondes après le relâchement, puis
 * redescend — voir `rememberSolidRotation`.
 *
 * **Décision de rendu : dessin Compose sur Canvas, avec faces pleines éclairées.**
 * Un moteur 3D pour un seul petit objet ajouterait une dépendance et une surface de
 * rendu à intégrer dans la composition, pour un gain nul ici. Une série d'images
 * pré-rendues figerait l'éclairage, ne suivrait pas la palette de l'ambiance et
 * pèserait en mémoire. Le calcul direct garde la maîtrise complète de la rotation,
 * de la lumière et des couleurs, sans dépendance.
 *
 * **Deux axes sont animés**, à des périodes volontairement non multiples l'une de
 * l'autre : le mouvement ne se répète donc jamais à l'identique et l'objet présente
 * successivement toutes ses faces. Avec un seul axe animé et une inclinaison figée,
 * la figure se lisait comme une image plate qui pivote.
 *
 * La projection est **perspective** et non orthographique : les sommets proches sont
 * plus écartés que les lointains, ce qui donne le volume. Les arêtes du fond sont en
 * plus atténuées, ce qui évite d'avoir à masquer les faces cachées.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DodecahedronButton(
    size: Dp,
    contentDescription: String,
    longPressLabel: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val haptics = LocalHapticFeedback.current
    val label = contentDescription
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    // L'accélération dure le temps de l'appui, puis se prolonge quelques secondes :
    // relâcher ne doit pas couper le mouvement net.
    var boosted by remember { mutableStateOf(false) }
    LaunchedEffect(pressed) {
        if (pressed) {
            boosted = true
        } else if (boosted) {
            delay(AstroMotion.SOLID_BOOST_TAIL_MILLIS)
            boosted = false
        }
    }

    val rotation = rememberSolidRotation(boosted)

    Canvas(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
                onLongClickLabel = longPressLabel,
                onLongClick = {
                    // Retour vibrant : sans ce signal, rien ne dit à l'utilisateur que
                    // le maintien a été pris en compte, l'écran changeant peu.
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                },
            )
            .semantics { this.contentDescription = label },
    ) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val boxRadius = this.size.minDimension / 2f
        val scale = boxRadius * 0.66f

        // Halo calé sur le cadre : le solide doit paraître suspendu, et la découpe
        // circulaire du bouton trancherait net un dégradé plus large.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.accentCosmic.copy(alpha = 0.22f), Color.Transparent),
                center = center,
                radius = boxRadius,
            ),
            radius = boxRadius,
            center = center,
        )
        drawDodecahedron(
            yaw = rotation.yaw,
            pitch = rotation.pitch,
            roll = rotation.roll,
            center = center,
            scale = scale,
            strokeWidth = dimens.hairline.toPx(),
            darkFace = palette.skyTop,
            litFace = palette.accentCosmic,
            edge = palette.accentLunar,
        )
    }
}
