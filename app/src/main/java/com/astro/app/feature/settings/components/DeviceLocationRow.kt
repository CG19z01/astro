package com.astro.app.feature.settings.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.feature.settings.LocationRequest

/**
 * Proposition d'utiliser la position de l'appareil.
 *
 * L'autorisation est demandée au moment de l'appui, jamais au lancement : l'utilisateur
 * doit comprendre à quoi elle sert quand on la lui demande.
 *
 * Les deux niveaux de précision sont demandés ensemble. Android laisse alors le choix
 * entre position approximative et précise ; l'approximative suffit ici, mais la refuser
 * d'office priverait les appareils sans localisation réseau.
 */
@Composable
fun DeviceLocationRow(
    label: String,
    description: String,
    actionLabel: String,
    enabled: Boolean,
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        if (results.values.any { it }) onGranted() else onDenied()
    }

    SettingRow(label = label, description = description, modifier = modifier) {
        TextButton(
            onClick = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ),
                )
            },
            enabled = enabled,
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) palette.accentSolar else palette.inkFaint,
            )
        }
    }
}

/** Ligne affichée quand une position de session est active : elle rappelle qu'elle est éphémère. */
@Composable
fun SessionLocationRow(
    label: String,
    description: String,
    forgetLabel: String,
    onForget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    SettingRow(label = label, description = description, modifier = modifier) {
        TextButton(onClick = onForget) {
            Text(
                text = forgetLabel,
                style = MaterialTheme.typography.labelLarge,
                color = palette.accentSolar,
            )
        }
    }
}

/** Message accompagnant la proposition, selon l'issue de la dernière demande. */
fun locationRequestHint(state: LocationRequest): Int = when (state) {
    LocationRequest.IDLE -> R.string.settings_use_device_location_hint
    LocationRequest.LOADING -> R.string.settings_location_loading
    LocationRequest.UNAVAILABLE -> R.string.settings_location_unavailable
    LocationRequest.DENIED -> R.string.settings_location_denied
}

/**
 * D'où vient la position : celle de la session si elle existe (avec de quoi l'oublier),
 * sinon la proposition d'utiliser celle de l'appareil.
 */
@Composable
fun LocationSourceRow(
    hasSessionPoint: Boolean,
    request: LocationRequest,
    onForget: () -> Unit,
    onGranted: () -> Unit,
    onDenied: () -> Unit,
) {
    if (hasSessionPoint) {
        SessionLocationRow(
            label = stringResource(R.string.settings_location_active),
            description = stringResource(R.string.settings_location_active_hint),
            forgetLabel = stringResource(R.string.settings_location_forget),
            onForget = onForget,
        )
    } else {
        DeviceLocationRow(
            label = stringResource(R.string.settings_use_device_location),
            description = stringResource(locationRequestHint(request)),
            actionLabel = stringResource(R.string.settings_device_location_action),
            enabled = request != LocationRequest.LOADING,
            onGranted = onGranted,
            onDenied = onDenied,
        )
    }
}
