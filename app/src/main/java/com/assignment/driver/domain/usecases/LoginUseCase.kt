package com.assignment.driver.domain.usecases

import com.assignment.driver.data.repository.DriverRepository
import com.assignment.driver.data.session.SessionStore
import com.assignment.driver.domain.models.Driver
import com.assignment.driver.domain.session.SessionManager
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val driverRepo: DriverRepository,
    private val session: SessionManager,
    private val sessionStore: SessionStore
) {
    // Offline login against seeded/current driver
    suspend fun loginOffline(username: String): Result<Driver> {
        val current = driverRepo.getCurrent()
        return if (current != null && username.equals(current.id, true)) {
            session.setDriver(current)
            sessionStore.setDriverId(current.id)
            Result.success(current)
        } else {
            Result.failure(IllegalArgumentException("Invalid credentials (offline)"))
        }
    }

    suspend fun logout() {
        session.setDriver(null)
        sessionStore.setDriverId(null)
    }

    fun sessionDriver() = session.currentDriver

    // Restore on app start
    suspend fun restoreSession(): Boolean {
        val id = sessionStore.getDriverId() ?: return false
        val current = driverRepo.getCurrent()
        if (current != null && current.id == id) {
            session.setDriver(current)
            return true
        }
        return false
    }
}
