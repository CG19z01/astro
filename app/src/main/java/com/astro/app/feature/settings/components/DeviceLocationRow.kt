package com.astro.app.feature.settings.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme

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
