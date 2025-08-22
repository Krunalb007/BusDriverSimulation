package com.assignment.driver.domain.usecases

import android.util.Log
import com.assignment.driver.data.repository.TripRepository
import javax.inject.Inject

class EndTripUseCase @Inject constructor(
    private val tripRepo: TripRepository,
    private val enqueueSync: EnqueueSyncUseCase
) {
    suspend fun end(tripId: String, endTime: Long = System.currentTimeMillis()) {
        tripRepo.completeTrip(tripId, endTime)
        // After completeTrip:
        val ended = tripRepo.getTripById(tripId)
        timber.log.Timber.i("Ended trip ${ended?.id} endTime=${ended?.endTime} status=${ended?.status}")

        enqueueSync.fireOnce()
    }
}

