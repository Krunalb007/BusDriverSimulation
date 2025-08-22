package com.assignment.driver.data.repository

import com.assignment.driver.domain.models.Driver
import kotlinx.coroutines.flow.Flow

interface DriverRepository {
    fun observeCurrent(): Flow<Driver?>
    suspend fun getCurrent(): Driver?
    suspend fun upsert(driver: Driver)
}
