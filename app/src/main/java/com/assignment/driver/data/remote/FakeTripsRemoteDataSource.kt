package com.assignment.driver.data.remote

import com.assignment.driver.BuildConfig
import com.assignment.driver.data.remote.dto.TripUploadDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Fake implementation of TripsRemoteDataSource to simulate trip uploads.
 *
 * Behavior:
 * - Adds artificial latency to emulate a real network request.
 * - Optionally injects random failures when SIMULATE_FAILURES is true (debug builds).
 * - Otherwise returns success(Unit).
 *
 * Use cases:
 * - Enables end-to-end testing of the sync pipeline without a live backend.
 * - Allows WorkManager retry/backoff behavior to be exercised in development.
 */
@Singleton
class FakeTripsRemoteDataSource @Inject constructor() : TripsRemoteDataSource {

    override suspend fun uploadTrip(payload: TripUploadDto): Result<Unit> {
        // Simulate network latency
        delay(800L)

        // Optionally simulate random failures in debug
        if (BuildConfig.SIMULATE_FAILURES && Random.nextInt(100) < 25) {
            return Result.failure(IllegalStateException("Simulated server failure"))
        }

        // Always succeed otherwise
        return Result.success(Unit)
    }
}
