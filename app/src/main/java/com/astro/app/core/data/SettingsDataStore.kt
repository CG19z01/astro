package com.astro.app.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** Nom du fichier de préférences sur l'appareil. */
private const val SETTINGS_DATASTORE_NAME = "astro_settings"

/**
 * Instance unique de DataStore pour les préférences de l'application.
 * Le délégué garantit qu'un seul DataStore existe par processus pour ce nom.
 */
val Context.settingsDataStore: DataStore<Preferences> by
    preferencesDataStore(name = SETTINGS_DATASTORE_NAME)
