package com.assignment.driver.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.assignment.driver.domain.usecases.RefreshCatalogUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * CatalogSyncWorker
 *
 * One-time worker that refreshes lightweight catalog data (driver display name, routes)
 * when connectivity is available.
 *
 * Triggers:
 * - Typically enqueued on app start or after login.
 *
 * Constraints:
 * - Requires NetworkType.CONNECTED to run.
 *
 * Behavior:
 * - Calls RefreshCatalogUseCase to fetch and upsert latest driver/routes into Room.
 * - Logs success; retries on failures.
 */
@HiltWorker
class CatalogSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val refreshCatalog: RefreshCatalogUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val ok = refreshCatalog.refreshIfOnline()
            Timber.i("Catalog refresh finished: $ok")
            Result.success()
        } catch (t: Throwable) {
            Timber.w(t, "Catalog refresh failed")
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "catalog_sync_one_time"

        /**
         * Enqueue a constrained one-time refresh job.
         */
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val req = OneTimeWorkRequestBuilder<CatalogSyncWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, req)
        }
    }
}
