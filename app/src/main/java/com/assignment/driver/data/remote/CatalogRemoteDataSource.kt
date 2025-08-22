package com.assignment.driver.data.remote

import com.assignment.driver.data.remote.dto.RouteDto

interface CatalogRemoteDataSource {
    suspend fun fetchDriver(driverId: String): Result<Pair<String, String>> // id, name
    suspend fun fetchRoutes(): Result<List<RouteDto>>
}
