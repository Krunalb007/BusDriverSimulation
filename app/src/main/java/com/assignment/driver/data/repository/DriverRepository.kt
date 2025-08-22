package com.assignment.driver.data.repository

import com.assignment.driver.domain.models.Driver
import kotlinx.coroutines.flow.Flow

/**
 * DriverRepository
 *
 * Contract for managing the current driver profile.
 *
 * Methods:
 * - observeCurrent(): reactive stream of the current driver (null if none).
 * - getCurrent(): one-shot fetch of the current driver.
 * - upsert(driver): create or update the current driver locally.
 *
 * Notes:
 * - Designed for offline-first usage; remote sync can update via upsert.
 */
interface DriverRepository {
    fun observeCurrent(): Flow<Driver?>
    suspend fun getCurrent(): Driver?
    suspend fun upsert(driver: Driver)
}
