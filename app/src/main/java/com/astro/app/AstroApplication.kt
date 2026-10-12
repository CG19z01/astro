package com.astro.app

import android.app.Application
import com.astro.app.core.alarm.SystemNextAlarmSource
import com.astro.app.core.data.DataStorePlanningRepository
import com.astro.app.core.data.FileManifestationRepository
import com.astro.app.core.data.FilePhraseRepository
import com.astro.app.core.data.DataStoreSettingsRepository
import com.astro.app.core.data.settingsDataStore
import com.astro.app.core.di.AppContainer
import com.astro.app.core.location.DeviceLocationProvider
import com.astro.app.core.location.SessionLocation
import com.astro.app.core.speech.AndroidSpeechService
import java.io.File

/** Point de construction des dépendances, à la racine du cycle de vie de l'application. */
class AstroApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(
            settingsRepository = DataStoreSettingsRepository(settingsDataStore),
            planningRepository = DataStorePlanningRepository(settingsDataStore),
            locationProvider = DeviceLocationProvider(applicationContext),
            sessionLocation = SessionLocation(),
            nextAlarmSource = SystemNextAlarmSource(applicationContext),
            phraseRepository = FilePhraseRepository(File(filesDir, "phrases")),
            manifestationRepository = FileManifestationRepository(File(filesDir, "manifestations")),
            speechService = AndroidSpeechService(applicationContext),
        )
    }
}
