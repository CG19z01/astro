package com.astro.app

import android.app.Application
import com.astro.app.core.data.DataStorePlanningRepository
import com.astro.app.core.data.DataStoreSettingsRepository
import com.astro.app.core.data.settingsDataStore
import com.astro.app.core.di.AppContainer

/** Point de construction des dépendances, à la racine du cycle de vie de l'application. */
class AstroApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(
            settingsRepository = DataStoreSettingsRepository(settingsDataStore),
            planningRepository = DataStorePlanningRepository(settingsDataStore),
        )
    }
}
