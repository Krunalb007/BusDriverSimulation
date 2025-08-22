package com.assignment.driver.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.assignment.driver.data.remote.TripsRemoteDataSource
import com.assignment.driver.data.remote.mappers.toUploadDto
import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.domain.models.TripStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class TripSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tripsRemote: TripsRemoteDataSource,
    private val tripRepo: TripRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Timber.i("TripSyncWorker started")
        try {
            val pending = tripRepo.getTripsByStatus(TripStatus.COMPLETED)
            if (pending.isEmpty()) return@withContext Result.success()

            for (trip in pending) {
                val locations = tripRepo.getLocations(trip.id)
                val payload = toUploadDto(trip, locations)
                val res = tripsRemote.uploadTrip(payload)
                if (res.isSuccess) {
                    tripRepo.updateTripStatus(trip.id, TripStatus.SYNCED)
                     tripRepo.clearLocations(trip.id)
                } else {
                    Timber.w("Upload failed for trip ${trip.id}, retrying later")
                    return@withContext Result.retry()
                }
            }
            Result.success()
        } catch (t: Throwable) {
            Timber.e(t, "TripSyncWorker failed")
            Result.retry()
        }
    }

    companion object {
        const val TAG = "trip_sync"
        private const val UNIQUE_ONE_TIME = "trip_sync_one_time"
        private const val UNIQUE_PERIODIC = "trip_sync_periodic"

        private fun connectedConstraints() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun enqueueOneTime(context: Context) {
            val request = OneTimeWorkRequestBuilder<TripSyncWorker>()
                .setConstraints(connectedConstraints())
                .addTag(TAG)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_ONE_TIME, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }

        fun ensurePeriodic(context: Context) {
            val periodic = PeriodicWorkRequestBuilder<TripSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(connectedConstraints())
                .addTag(TAG)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_PERIODIC, ExistingPeriodicWorkPolicy.KEEP, periodic)
        }
    }

}
