package com.assignment.driver.domain.usecases

import com.assignment.driver.data.remote.CatalogRemoteDataSource
import com.assignment.driver.data.remote.mappers.toDomain
import com.assignment.driver.data.repository.DriverRepository
import com.assignment.driver.data.repository.RouteRepository
import com.assignment.driver.domain.models.Driver
import com.assignment.driver.domain.session.SessionManager
import javax.inject.Inject

class RefreshCatalogUseCase @Inject constructor(
    private val catalog: CatalogRemoteDataSource,
    private val driverRepo: DriverRepository,
    private val routeRepo: RouteRepository,
    private val session: SessionManager
) {
    suspend fun refreshIfOnline(): Boolean {
        var any = false

        val driver = session.currentDriver.value
        if (driver != null) {
            val res = catalog.fetchDriver(driver.id)
            if (res.isSuccess) {
                val (id, name) = res.getOrThrow()
                driverRepo.upsert(Driver(id, name, System.currentTimeMillis()))
                any = true
            }
        }

        val routesRes = catalog.fetchRoutes()
        if (routesRes.isSuccess) {
            val list = routesRes.getOrThrow().map { it.toDomain() }
            routeRepo.replaceAll(list)
            any = true
        }

        return any
    }
}