package com.astro.app.feature.planning.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme

/** Colonnes et rangées de points de la poignée. */
private const val GRIP_COLUMNS = 2
private const val GRIP_ROWS = 3

/**
 * Poignée d'une note : six points, le signe usuel de « ceci se déplace ».
 *
 * [handle] porte le geste (appui long puis glisser). Pour les lecteurs d'écran, qui ne
 * peuvent pas faire glisser, deux actions « Monter » et « Descendre » font la même chose
 * d'un cran. Sans [handle] — la ligne vide finale —, la poignée laisse sa place vide pour
 * que les lignes restent alignées.
 */
@Composable
fun DragGrip(handle: Modifier?, onNudge: (Int) -> Unit) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    if (handle == null) {
        Box(Modifier.width(dimens.iconMd))
        return
    }
    val description = stringResource(R.string.planning_drag_handle)
    val up = stringResource(R.string.planning_move_up)
    val down = stringResource(R.string.planning_move_down)
    Box(
        modifier = handle
            .size(dimens.iconMd, dimens.touchTarget)
            .semantics {
                contentDescription = description
                customActions = listOf(
                    CustomAccessibilityAction(up) { onNudge(-1); true },
                    CustomAccessibilityAction(down) { onNudge(1); true },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(dimens.iconSm)) {
            val dot = size.minDimension / 12f
            for (column in 0 until GRIP_COLUMNS) {
                for (row in 0 until GRIP_ROWS) {
                    val x = size.width * (column + 1) / (GRIP_COLUMNS + 1)
                    val y = size.height * (row + 1) / (GRIP_ROWS + 1)
                    drawCircle(color = palette.inkFaint, radius = dot, center = Offset(x, y))
                }
            }
        }
    }
}
