package com.assignment.driver.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.assignment.driver.BuildConfig
import com.assignment.driver.data.local.SeedManager
import com.assignment.driver.domain.usecases.EnqueueCatalogSyncUseCase
import com.assignment.driver.domain.usecases.LoginUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BusDriverApp : Application(), Configuration.Provider {

    @Inject
    lateinit var seedManager: SeedManager
    @Inject
    lateinit var enqueueCatalog: EnqueueCatalogSyncUseCase
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        seedManager.seedIfNeeded()

        GlobalScope.launch(Dispatchers.IO) {
            loginUseCase.restoreSession()
            enqueueCatalog.fireOnce()
        }
    }

    override val workManagerConfiguration: Configuration
        get() {
            Timber.i("Providing WorkManager configuration with HiltWorkerFactory")
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }
}
