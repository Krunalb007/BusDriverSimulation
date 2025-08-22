package com.assignment.driver.data.session

import com.assignment.driver.data.local.dao.SessionDao
import com.assignment.driver.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor(
    private val sessionDao: SessionDao
) {
    suspend fun setDriverId(id: String?) {
        sessionDao.upsert(SessionEntity(driverId = id))
    }

    suspend fun getDriverId(): String? {
        return sessionDao.get()?.driverId
    }

    fun observeDriverId(): Flow<String?> =
        sessionDao.observe().map { it?.driverId }
}
