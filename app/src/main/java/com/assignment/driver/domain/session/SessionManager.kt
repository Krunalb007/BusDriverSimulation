package com.assignment.driver.domain.session

import com.assignment.driver.domain.models.Driver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SessionManager
 *
 * Purpose:
 * - Holds the in-memory representation of the current authenticated driver.
 * - Exposes a reactive StateFlow so UI and domain layers can react to login/logout changes.
 *
 * Responsibilities:
 * - Keep track of the current Driver (or null when logged out).
 * - Provide a thread-safe, observable state container for session-aware features.
 *
 * Notes:
 * - This is in-memory only. Persistence is handled by SessionStore, which stores the driverId
 *   in Room so sessions can be restored across process/app restarts.
 * - Always call setDriver(...) after login/logout flows to keep UI consistent.
 */
@Singleton
class SessionManager @Inject constructor() {

    private val _currentDriver = MutableStateFlow<Driver?>(null)

    /**
     * Public, read-only view of the current driver session.
     * Emits null when logged out.
     */
    val currentDriver: StateFlow<Driver?> = _currentDriver

    /**
     * Update the current driver (null to clear on logout).
     */
    fun setDriver(driver: Driver?) {
        _currentDriver.value = driver
    }
}
