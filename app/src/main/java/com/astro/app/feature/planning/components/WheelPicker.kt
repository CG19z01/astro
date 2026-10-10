package com.astro.app.feature.planning.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.designsystem.AstroTheme

/**
 * Roue de valeurs : on la fait défiler, la valeur du centre est retenue.
 *
 * Construite sur une liste paresseuse et le « snapping » de Compose Foundation, déjà
 * présents dans le projet : aucune dépendance ajoutée. Les marges haute et basse valent
 * la moitié de la hauteur visible, si bien que la première et la dernière valeur
 * peuvent elles aussi venir au centre.
 *
 * [startIndex] ne sert qu'à l'ouverture : la roue garde ensuite sa propre position,
 * stable d'une recomposition à l'autre.
 */
@Composable
fun WheelPicker(
    values: List<Int>,
    startIndex: Int,
    description: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val itemHeight = PlanningConfig.WHEEL_ITEM_HEIGHT
    val sidePadding = itemHeight * (PlanningConfig.WHEEL_VISIBLE_ITEMS / 2)
    val state = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    // L'élément au centre : le premier visible, plus un s'il est déjà à moitié passé.
    val centered by remember {
        derivedStateOf {
            val index = state.firstVisibleItemIndex +
                if (state.firstVisibleItemScrollOffset > itemHeightPx / 2) 1 else 0
            index.coerceIn(values.indices)
        }
    }
    LaunchedEffect(state) {
        snapshotFlow { centered }.collect { onSelect(values[it]) }
    }

    Box(
        modifier = modifier
            .width(PlanningConfig.WHEEL_WIDTH)
            .height(itemHeight * PlanningConfig.WHEEL_VISIBLE_ITEMS)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        // Cadre de la valeur retenue, derrière la roue.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .border(AstroTheme.dimens.hairline, palette.accentSolar, MaterialTheme.shapes.small),
        )
        LazyColumn(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
            contentPadding = PaddingValues(vertical = sidePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(values) { index, value ->
                Box(modifier = Modifier.height(itemHeight), contentAlignment = Alignment.Center) {
                    Text(
                        text = twoDigits(value),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (index == centered) palette.ink else palette.inkFaint,
                    )
                }
            }
        }
    }
}

/** Une valeur sur deux chiffres : « 05 », « 23 ». */
internal fun twoDigits(value: Int): String = value.toString().padStart(2, '0')
