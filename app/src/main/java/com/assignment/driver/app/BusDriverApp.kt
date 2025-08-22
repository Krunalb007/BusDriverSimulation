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


/**
 * Application entry-point for the Bus Driver app.
 *
 * Responsibilities:
 * - Bootstraps global dependencies via Hilt (annotated with @HiltAndroidApp).
 * - Provides a custom WorkManager Configuration that supplies Hilt’s WorkerFactory,
 *   ensuring Workers annotated with @HiltWorker are constructed through DI.
 * - Seeds initial local data on first run (drivers, routes, etc.) through [SeedManager].
 * - Restores a previously logged-in session on cold start so the app is usable offline
 *   without requiring the user to log in again (via [LoginUseCase.restoreSession]).
 * - Triggers a one-time catalog refresh (driver/routes) when connectivity is available,
 *   writing updates into Room and keeping the UI reactive (via [EnqueueCatalogSyncUseCase]).
 *
 * Threading:
 * - Uses a lightweight background coroutine at startup for non-blocking tasks
 *   (session restore and catalog sync enqueue). These are fire-and-forget, IO-bound operations.
 *
 * WorkManager integration:
 * - Implements [androidx.work.Configuration.Provider] and overrides
 *   [workManagerConfiguration] to return a [Configuration] that sets the injected
 *   [HiltWorkerFactory]. This is critical: it allows WorkManager to create Workers
 *   with dependencies provided by Hilt instead of falling back to reflection.
 *
 * Logging:
 * - Plants a Timber DebugTree in debug builds for structured logging.
 *
 * Notes:
 * - Startup work is intentionally minimal to keep app launch fast. Heavy or long-running
 *   work should be deferred to WorkManager.
 * - If you later add any library that auto-initializes WorkManager before this provider
 *   runs, ensure WorkManager uses this configuration (e.g., by disabling conflicting
 *   initializers or initializing explicitly very early in the app lifecycle).
 */

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
