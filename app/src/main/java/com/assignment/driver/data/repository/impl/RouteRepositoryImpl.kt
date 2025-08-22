package com.assignment.driver.data.repository.impl

import com.assignment.driver.data.local.dao.RouteDao
import com.assignment.driver.data.mappers.toDomain
import com.assignment.driver.data.mappers.toEntity
import com.assignment.driver.data.repository.RouteRepository
import com.assignment.driver.domain.models.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * RouteRepositoryImpl
 *
 * Purpose:
 * - Implements RouteRepository using Room’s RouteDao.
 * - Acts as the single source of truth for the route catalog locally.
 *
 * Responsibilities:
 * - observeRoutes(): reactive stream of all routes (for UI lists).
 * - getRoutes(): one-shot retrieval of all routes.
 * - replaceAll(routes): upsert/replace the catalog with provided routes.
 * - clear(): remove all routes (used for resets/tests).
 *
 * Notes:
 * - Remote refresh flows should call replaceAll(...) to update the catalog.
 * - Mapping to/from domain is kept in mappers for consistency.
 */
class RouteRepositoryImpl @Inject constructor(
    private val dao: RouteDao
) : RouteRepository {

    override fun observeRoutes(): Flow<List<Route>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getRoutes(): List<Route> =
        dao.getAll().map { it.toDomain() }

    override suspend fun replaceAll(routes: List<Route>) {
        dao.upsertAll(routes.map { it.toEntity() })
    }

    override suspend fun clear() {
        dao.clear()
    }
}
