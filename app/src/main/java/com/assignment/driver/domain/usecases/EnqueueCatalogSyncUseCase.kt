package com.assignment.driver.domain.usecases

import android.content.Context
import com.assignment.driver.sync.CatalogSyncWorker
import javax.inject.Inject

class EnqueueCatalogSyncUseCase @Inject constructor(
    private val appContext: Context
) {
    fun fireOnce() = CatalogSyncWorker.enqueue(appContext)
}
