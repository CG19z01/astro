package com.astro.app.feature.home.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.astro.app.R
import com.astro.app.core.alarm.WakeAlarm
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.planning.PlanningCodec

/**
 * Premier réveil de la journée, lu dans l'application Horloge via Android.
 *
 * Écrit AU-DESSUS du jour, l'heure seule et en plus grand (demande du 2026-10-10) :
 * « 07:15 ». Le mot « Réveil » reste dit au lecteur d'écran.
 */
@Composable
fun PlanetAlarmLine(alarm: WakeAlarm, modifier: Modifier = Modifier) {
    val style = alarmStyle(alarm) ?: return
    val description: String
    val text = when (alarm) {
        is WakeAlarm.At -> alarm.time.format(PlanningCodec.TIME_FORMAT).also {
            description = stringResource(R.string.home_alarm_at, it)
        }
        else -> stringResource(R.string.home_alarm_none).also { description = it }
    }
    Text(
        text = text,
        style = style,
        color = if (alarm is WakeAlarm.At) AstroTheme.palette.accentLunar else AstroTheme.palette.inkFaint,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.semantics { contentDescription = description },
    )
}

/** Style de la ligne du réveil ; `null` quand rien n'est affiché. */
@Composable
fun alarmStyle(alarm: WakeAlarm): TextStyle? = when (alarm) {
    is WakeAlarm.At -> MaterialTheme.typography.titleMedium
    WakeAlarm.None -> MaterialTheme.typography.bodySmall
    WakeAlarm.Unknown -> null
}
