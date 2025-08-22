package com.assignment.driver.data.repository.impl

import com.assignment.driver.data.local.dao.DriverDao
import com.assignment.driver.data.mappers.toDomain
import com.assignment.driver.data.mappers.toEntity
import com.assignment.driver.data.repository.DriverRepository
import com.assignment.driver.domain.models.Driver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * DriverRepositoryImpl
 *
 * Purpose:
 * - Implements DriverRepository on top of Room’s DriverDao.
 * - Provides a clean API for observing and updating the current driver profile.
 *
 * Responsibilities:
 * - observeCurrent(): reactive stream of the current driver (null when none).
 * - getCurrent(): fetch the current driver synchronously.
 * - upsert(driver): insert or update the current driver in local storage.
 *
 * Notes:
 * - Mapping between Room entity and domain model is centralized in mappers.
 * - Designed for offline-first behavior; a remote sync layer can upsert when online.
 */
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
