package com.assignment.driver.data.remote

import com.assignment.driver.data.remote.dto.RouteDto

/**
 * Abstraction for fetching catalog data (driver profile and routes) from a remote source.
 *
 * Contract:
 * - fetchDriver(driverId): returns a pair (id, name) inside a Result wrapper.
 * - fetchRoutes(): returns a list of RouteDto inside a Result wrapper.
 *
 * Error handling:
 * - Failures are propagated via Result.failure(...) so callers can decide on retries/backoff.
 *
 * Implementation:
 * - Production apps would implement this with Retrofit (or similar).
 * - Tests/demo can use a fake implementation.
 */
interface CatalogRemoteDataSource {
    suspend fun fetchDriver(driverId: String): Result<Pair<String, String>> // id, name
    suspend fun fetchRoutes(): Result<List<RouteDto>>
}
