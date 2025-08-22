package com.assignment.driver.domain.usecases


import com.assignment.driver.data.repository.RouteRepository
import com.assignment.driver.domain.models.Route
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutesUseCase @Inject constructor(
    private val routeRepo: RouteRepository
) {
    fun observeRoutes(): Flow<List<Route>> = routeRepo.observeRoutes()
    suspend fun getRoutes(): List<Route> = routeRepo.getRoutes()
}
