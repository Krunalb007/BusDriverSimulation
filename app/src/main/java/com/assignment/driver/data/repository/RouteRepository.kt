package com.assignment.driver.data.repository

import com.assignment.driver.domain.models.Route
import kotlinx.coroutines.flow.Flow

/**
 * RouteRepository
 *
 * Contract for accessing and maintaining the local route catalog.
 *
 * Methods:
 * - observeRoutes(): reactive stream of all routes for UI.
 * - getRoutes(): one-shot retrieval of all routes.
 * - replaceAll(routes): upsert/replace the route catalog.
 * - clear(): remove all routes.
 *
 * Notes:
 * - Typically refreshed from a remote source when online.
 */
interface RouteRepository {
    fun observeRoutes(): Flow<List<Route>>
    suspend fun getRoutes(): List<Route>
    suspend fun replaceAll(routes: List<Route>)
    suspend fun clear()
}
