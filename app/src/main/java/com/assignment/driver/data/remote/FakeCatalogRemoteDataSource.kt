package com.assignment.driver.data.remote

import com.assignment.driver.data.remote.dto.RouteDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of CatalogRemoteDataSource for local development and tests.
 *
 * Behavior:
 * - Simulates latency via delay(...) to mimic network calls.
 * - Returns a static driver name for any driverId.
 * - Returns a small, static list of routes stamped with the current time.
 *
 * Usage:
 * - Bind this implementation in DI for debug builds or offline demos.
 */
@Singleton
class FakeCatalogRemoteDataSource @Inject constructor() : CatalogRemoteDataSource {

    override suspend fun fetchDriver(driverId: String): Result<Pair<String, String>> {
        delay(400L)
        return Result.success(driverId to "Alex Driver")
    }

    override suspend fun fetchRoutes(): Result<List<RouteDto>> {
        delay(600L)
        val now = System.currentTimeMillis()
        return Result.success(
            listOf(
                RouteDto("route-101", "City Center Loop", "Central Station", "Old Town", now),
                RouteDto("route-102", "Airport Express", "Central Station", "International Airport", now),
                RouteDto("route-103", "Tech Park Shuttle", "Central Station", "Tech Park", now)
            )
        )
    }
}
