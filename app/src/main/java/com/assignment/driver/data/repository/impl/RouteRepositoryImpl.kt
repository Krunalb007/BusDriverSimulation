package com.assignment.driver.data.repository.impl

import com.assignment.driver.data.local.dao.RouteDao
import com.assignment.driver.data.mappers.toDomain
import com.assignment.driver.data.mappers.toEntity
import com.assignment.driver.data.repository.RouteRepository
import com.assignment.driver.domain.models.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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
