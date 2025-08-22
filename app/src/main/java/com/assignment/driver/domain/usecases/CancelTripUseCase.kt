package com.assignment.driver.domain.usecases

import android.content.Context
import androidx.work.WorkManager
import com.assignment.driver.sync.TripSyncWorker
import javax.inject.Inject

class CancelTripSyncUseCase @Inject constructor(
    private val appContext: Context
) {
    fun cancelAll() {
        WorkManager.getInstance(appContext).cancelAllWorkByTag(TripSyncWorker.TAG)
    }
}
