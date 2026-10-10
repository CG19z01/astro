package com.astro.app.feature.home.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    // Sans réveil ce jour-là, rien n'est écrit (« Aucun réveil » retiré le 2026-10-10,
    // à la demande de l'utilisateur).
    if (alarm !is WakeAlarm.At) return
    val time = alarm.time.format(PlanningCodec.TIME_FORMAT)
    val description = stringResource(R.string.home_alarm_at, time)
    Text(
        text = time,
        style = MaterialTheme.typography.titleMedium,
        color = AstroTheme.palette.accentLunar,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.semantics { contentDescription = description },
    )
}
