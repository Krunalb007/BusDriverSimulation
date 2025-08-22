package com.assignment.driver.domain.usecases

import android.content.Context
import com.assignment.driver.sync.TripSyncWorker
import javax.inject.Inject

class EnqueueSyncUseCase @Inject constructor(
    private val appContext: Context
) {
    fun fireOnce() = TripSyncWorker.enqueueOneTime(appContext)
    fun ensurePeriodic() = TripSyncWorker.ensurePeriodic(appContext)
}
