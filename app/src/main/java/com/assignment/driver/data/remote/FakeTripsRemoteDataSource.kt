package com.assignment.driver.data.remote


import com.assignment.driver.BuildConfig
import com.assignment.driver.data.remote.TripsRemoteDataSource
import com.assignment.driver.data.remote.dto.TripUploadDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

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
