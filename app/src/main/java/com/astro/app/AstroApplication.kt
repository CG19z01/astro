package com.astro.app

import android.app.Application
import com.astro.app.core.alarm.SystemNextAlarmSource
import com.astro.app.core.data.DataStorePlanningRepository
import com.astro.app.core.data.DataStoreSettingsRepository
import com.astro.app.core.data.settingsDataStore
import com.astro.app.core.di.AppContainer
import com.astro.app.core.location.DeviceLocationProvider
import com.astro.app.core.location.SessionLocation

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
        )
    }
}
