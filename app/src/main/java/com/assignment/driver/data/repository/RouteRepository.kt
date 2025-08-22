package com.assignment.driver.data.repository

import com.assignment.driver.domain.models.Route
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun observeRoutes(): Flow<List<Route>>
    suspend fun getRoutes(): List<Route>
    suspend fun replaceAll(routes: List<Route>)
    suspend fun clear()
}
