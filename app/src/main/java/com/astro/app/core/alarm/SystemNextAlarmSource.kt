package com.astro.app.core.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import java.time.Instant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Prochain réveil lu auprès d'Android (`AlarmManager.getNextAlarmClock`).
 *
 * **Ce que l'on obtient vraiment.** Android ne donne accès à la liste des alarmes d'aucune
 * application, pas même de l'application Horloge. Il expose seulement le PROCHAIN réveil
 * programmé sur l'appareil, toutes applications confondues — en pratique celui de
 * l'Horloge, qui est l'application de réveil. Aucune permission n'est nécessaire.
 *
 * La valeur est relue chaque fois qu'Android annonce un changement
 * (`ACTION_NEXT_ALARM_CLOCK_CHANGED`) : réveil ajouté, modifié, coupé ou sonné.
 */
class SystemNextAlarmSource(private val context: Context) : NextAlarmSource {

    override fun nextAlarm(): Flow<Instant?> = callbackFlow {
        val alarms = context.getSystemService(AlarmManager::class.java)
        fun read(): Instant? = alarms?.nextAlarmClock?.triggerTime?.let(Instant::ofEpochMilli)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(read())
            }
        }
        // Diffusion système protégée : reçue même par un récepteur non exporté.
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        trySend(read())
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()
}
