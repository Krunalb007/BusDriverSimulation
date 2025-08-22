package com.assignment.driver.data.repository.impl

import com.assignment.driver.data.local.dao.DriverDao
import com.assignment.driver.data.mappers.toDomain
import com.assignment.driver.data.mappers.toEntity
import com.assignment.driver.data.repository.DriverRepository
import com.assignment.driver.domain.models.Driver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(
    private val dao: DriverDao
) : DriverRepository {

    override fun observeCurrent(): Flow<Driver?> =
        dao.observeCurrentDriver().map { it?.toDomain() }

    override suspend fun getCurrent(): Driver? =
        dao.getCurrentDriver()?.toDomain()

    override suspend fun upsert(driver: Driver) {
        dao.upsert(driver.toEntity())
    }
}
